/*
 * Copyright (c) 2009-2016 Dmytro Pishchukhin (http://knowhowlab.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.knowhowlab.osgi.niis.utils;

import org.knowhowlab.osgi.niis.impl.NetworkAdapterImpl;
import org.knowhowlab.osgi.niis.model.RegistrationDTO;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.networkadapter.NetworkAdapter;

import java.net.NetworkInterface;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.knowhowlab.osgi.niis.utils.CompareUtils.deepEquals;
import static org.knowhowlab.osgi.niis.utils.NetworkProperties.read;

/**
 * @author dpishchukhin
 */
public class RegistrationManager {
    private Map<String, RegistrationDTO<NetworkAdapter>> networkAdapterRegistrations = new HashMap<>();

    private Functions.TriFunction<Class<NetworkAdapter>,
        NetworkAdapter, Dictionary,
        ServiceRegistration<NetworkAdapter>> networkAdapterRegisterFunction;

    public RegistrationManager(
        Functions.TriFunction<Class<NetworkAdapter>,
            NetworkAdapter,
            Dictionary,
            ServiceRegistration<NetworkAdapter>> networkAdapterRegisterFunction) {
        this.networkAdapterRegisterFunction = networkAdapterRegisterFunction;
    }

    public void updateServices(Enumeration<NetworkInterface> networkInterfaces) {
        // collect network interfaces from java
        Map<String, NetworkInterface> actualInterfaces = Collections.list(networkInterfaces)
            .stream().collect(Collectors.toMap(NetworkInterface::getName, Function.identity()));

        unregisterObsoleteServices(actualInterfaces);

        registerNewServices(actualInterfaces);

        updateExistingServices(actualInterfaces);
    }

    private void updateExistingServices(Map<String, NetworkInterface> actualInterfaces) {
        // find services that should be updated
        HashSet<String> candidatesToUpdate = new HashSet<>(actualInterfaces.keySet());
        candidatesToUpdate.retainAll(networkAdapterRegistrations.keySet());
        //noinspection SuspiciousMethodCalls
        candidatesToUpdate
            .stream()
            .map(actualInterfaces::get)
            .map(NetworkProperties::read)
            .filter(m -> !deepEquals(m, networkAdapterRegistrations.get(m.get(Constants.SERVICE_PID)).properties))
            .forEach(m -> {
                //noinspection SuspiciousMethodCalls
                RegistrationDTO<NetworkAdapter> dto = networkAdapterRegistrations.get(m.get(Constants.SERVICE_PID));
                dto.serviceRegistration.setProperties(m);
                dto.properties.clear();
                dto.properties.putAll(m);
            });
    }

    private void registerNewServices(Map<String, NetworkInterface> actualInterfaces) {
        // find services that should be registered
        HashSet<String> candidatesToRegister = new HashSet<>(actualInterfaces.keySet());
        candidatesToRegister.removeAll(networkAdapterRegistrations.keySet());
        // register
        candidatesToRegister
            .stream()
            .map(actualInterfaces::remove)
            .map(networkInterface -> {
                Hashtable<String, Object> props = read(networkInterface);
                NetworkAdapterImpl instance = new NetworkAdapterImpl(networkInterface,
                    () -> (String) props.get(NetworkAdapter.NETWORKADAPTER_TYPE));
                ServiceRegistration<NetworkAdapter> registration = networkAdapterRegisterFunction.apply(NetworkAdapter.class, instance, props);
                return new RegistrationDTO<>(instance, registration, props);
            })
            .forEach(r -> networkAdapterRegistrations.put(r.instance.getName(), r));
    }

    private void unregisterObsoleteServices(Map<String, NetworkInterface> actualInterfaces) {
        // find services that should be unregistered
        HashSet<String> candidatesToUnregister = new HashSet<>(networkAdapterRegistrations.keySet());
        candidatesToUnregister.removeAll(actualInterfaces.keySet());
        // unregister
        candidatesToUnregister
            .stream()
            .map(networkAdapterRegistrations::remove)
            .forEach(r -> r.serviceRegistration.unregister());
    }

    public void close() {
        networkAdapterRegistrations.values()
            .forEach(dto -> dto.serviceRegistration.unregister());
        networkAdapterRegistrations.clear();
    }
}
