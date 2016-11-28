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

import org.knowhowlab.osgi.niis.impl.AbstractInstance;
import org.osgi.dto.DTO;
import org.osgi.framework.ServiceRegistration;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.knowhowlab.osgi.niis.utils.CompareUtils.deepEquals;
import static org.osgi.framework.Constants.SERVICE_PID;

/**
 * @author dpishchukhin
 */
public class RegistrationManager<T, R, U extends AbstractInstance> {
    private Map<String, RegistrationDTO<R, U>> registrations = new HashMap<>();

    private Class<R> registrationClass;
    private PropertiesCollector<T> propertiesCollector;
    private Function<T, String> idFunction;
    private BiFunction<T, Map<String, Object>, U> newInstanceFunction;
    private Functions.TriFunction<Class<R>, U, Dictionary, ServiceRegistration<R>> registrationFunction;

    private RegistrationManager(Class<R> registrationClass,
                               PropertiesCollector<T> propertiesCollector,
                               Function<T, String> idFunction,
                               BiFunction<T, Map<String, Object>, U> newInstanceFunction,
                               Functions.TriFunction<Class<R>, U, Dictionary, ServiceRegistration<R>> registrationFunction) {
        this.registrationClass = registrationClass;
        this.propertiesCollector = propertiesCollector;
        this.idFunction = idFunction;
        this.newInstanceFunction = newInstanceFunction;
        this.registrationFunction = registrationFunction;
    }

    public void updateServices(List<T> instances) {
        // collect network interfaces from java
        Map<String, T> actualSources = instances.stream().collect(Collectors.toMap(idFunction, Function.identity()));

        unregisterObsoleteServices(actualSources);

        registerNewServices(actualSources);

        updateExistingServices(actualSources);
    }

    public void close() {
        registrations.values()
            .forEach(dto -> dto.serviceRegistration.unregister());
        registrations.clear();
    }

    Map<String, RegistrationDTO<R, U>> getRegistrations() {
        return registrations;
    }

    private void updateExistingServices(Map<String, T> actualInstances) {
        // find services that should be updated
        HashSet<String> candidatesToUpdate = new HashSet<>(actualInstances.keySet());
        candidatesToUpdate.retainAll(registrations.keySet());
        //noinspection SuspiciousMethodCalls
        candidatesToUpdate
            .stream()
            .map(actualInstances::get)
            .map(propertiesCollector::collect)
            .filter(m -> !deepEquals(m, registrations.get(m.get(SERVICE_PID)).properties))
            .forEach(m -> {
                //noinspection SuspiciousMethodCalls
                RegistrationDTO<R, U> dto = registrations.get(m.get(SERVICE_PID));
                dto.serviceRegistration.setProperties(m);
                dto.properties.clear();
                dto.properties.putAll(m);
            });
    }

    private void registerNewServices(Map<String, T> actualInstances) {
        // find services that should be registered
        HashSet<String> candidatesToRegister = new HashSet<>(actualInstances.keySet());
        candidatesToRegister.removeAll(registrations.keySet());
        // register
        candidatesToRegister
            .stream()
            .map(actualInstances::remove)
            .map(source -> {
                Hashtable<String, Object> props = propertiesCollector.collect(source);
                U newInstance = newInstanceFunction.apply(source, props);
                ServiceRegistration<R> registration = registrationFunction.apply(registrationClass, newInstance, props);
                return new RegistrationDTO<>(newInstance, registration, props);
            })
            .forEach(r -> registrations.put(r.instance.getId(), r));
    }

    private void unregisterObsoleteServices(Map<String, T> actualInstances) {
        // find services that should be unregistered
        HashSet<String> candidatesToUnregister = new HashSet<>(registrations.keySet());
        candidatesToUnregister.removeAll(actualInstances.keySet());
        // unregister
        candidatesToUnregister
            .stream()
            .map(registrations::remove)
            .forEach(r -> r.serviceRegistration.unregister());
    }

    public static class Builder<T, R, U extends AbstractInstance> {
        private Functions.TriFunction<Class<R>, U, Dictionary, ServiceRegistration<R>> registrationFunction;
        private PropertiesCollector<T> propertiesCollector;
        private Function<T, String> idFunction;
        private BiFunction<T, Map<String, Object>, U> newInstanceFunction;
        private Class<R> registrationClass;

        public Builder(Class<R> registrationClass) {
            this.registrationClass = registrationClass;
        }

        public Builder<T, R, U> withRegistrationFunction(Functions.TriFunction<Class<R>, U, Dictionary, ServiceRegistration<R>> registrationFunction) {
            this.registrationFunction = registrationFunction;
            return this;
        }

        public Builder<T, R, U> withPropertiesCollector(PropertiesCollector<T> propertiesCollector) {
            this.propertiesCollector = propertiesCollector;
            return this;
        }

        public Builder<T, R, U> withIdFunction(Function<T, String> idFunction) {
            this.idFunction = idFunction;
            return this;
        }

        public Builder<T, R, U> withNewInstanceFunction(BiFunction<T, Map<String, Object>, U> newInstanceFunction) {
            this.newInstanceFunction = newInstanceFunction;
            return this;
        }

        public RegistrationManager<T, R, U> build() {
            return new RegistrationManager<>(registrationClass, propertiesCollector, idFunction, newInstanceFunction, registrationFunction);
        }
    }

    static class RegistrationDTO<T, U extends AbstractInstance> extends DTO {
        final U instance;
        final ServiceRegistration<T> serviceRegistration;
        final Hashtable<String, Object> properties;

        RegistrationDTO(U instance, ServiceRegistration<T> serviceRegistration, Hashtable<String, Object> properties) {
            this.instance = instance;
            this.serviceRegistration = serviceRegistration;
            this.properties = properties;
        }
    }
}
