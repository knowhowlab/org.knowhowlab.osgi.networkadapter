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

package org.knowhowlab.osgi.niis;

import org.knowhowlab.osgi.niis.impl.NetworkAdapterImpl;
import org.knowhowlab.osgi.niis.impl.NetworkAddressImpl;
import org.knowhowlab.osgi.niis.registry.IPRegistry;
import org.knowhowlab.osgi.niis.utils.Functions;
import org.knowhowlab.osgi.niis.utils.PropertiesCollector;
import org.knowhowlab.osgi.niis.utils.RegistrationManager;
import org.knowhowlab.osgi.niis.utils.Utils;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.*;

import static java.util.Optional.ofNullable;
import static org.knowhowlab.osgi.niis.utils.Utils.getAddressForRegistry;
import static org.knowhowlab.osgi.niis.utils.Utils.isIPv4;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.networkadapter.NetworkAdapter.*;
import static org.osgi.service.networkadapter.NetworkAddress.*;

/**
 * @author dpishchukhin
 */
class NetworkServicesRegistrationManager {
    private Functions.TriFunction<Class<NetworkAddress>,
        NetworkAddressImpl, Dictionary, ServiceRegistration<NetworkAddress>> addressRegistrationFunction;
    private IPRegistry ipRegistry;

    private RegistrationManager<NetworkInterface, NetworkAdapter, NetworkAdapterImpl> networkInterfaceRegistrationManager;
    private Map<String, RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl>> interfaceAddressRegistrationManagers = new HashMap<>();

    public NetworkServicesRegistrationManager(Functions.TriFunction<Class<NetworkAdapter>, NetworkAdapterImpl, Dictionary, ServiceRegistration<NetworkAdapter>> adapterRegistrationFunction,
                                              Functions.TriFunction<Class<NetworkAddress>, NetworkAddressImpl, Dictionary, ServiceRegistration<NetworkAddress>> addressRegistrationFunction,
                                              IPRegistry ipRegistry) {
        this.addressRegistrationFunction = addressRegistrationFunction;
        this.ipRegistry = ipRegistry;

        networkInterfaceRegistrationManager = createNetworkInterfaceManager(adapterRegistrationFunction);
    }

    public void updateServices(List<NetworkInterface> networkInterfaces) {
        networkInterfaceRegistrationManager.updateServices(networkInterfaces);
    }

    public void close() {
        updateServices(Collections.emptyList());
    }

    private RegistrationManager<NetworkInterface, NetworkAdapter, NetworkAdapterImpl> createNetworkInterfaceManager(
        Functions.TriFunction<Class<NetworkAdapter>, NetworkAdapterImpl, Dictionary, ServiceRegistration<NetworkAdapter>> registrationTriFunction) {
        return new RegistrationManager.Builder<NetworkInterface, NetworkAdapter, NetworkAdapterImpl>(NetworkAdapter.class)
            .withRegistrationFunction(registrationTriFunction)
            .withIdFunction(NetworkInterface::getName)
            .withNewInstanceFunction(NetworkAdapterImpl::new)
            .withPropertiesCollector(
                new PropertiesCollector.Builder<NetworkInterface>()
                    .addProperty(NetworkAdapter.NETWORKADAPTER_TYPE,
                        t -> ipRegistry.getType(getAddressForRegistry(t)))
                    .addProperty(NETWORKADAPTER_HARDWAREADDRESS,
                        NetworkInterface::getHardwareAddress, EMPTY_BYTE_ARRAY)
                    .addProperty(NETWORKADAPTER_NAME, NetworkInterface::getName)
                    .addProperty(NETWORKADAPTER_DISPLAYNAME,
                        NetworkInterface::getDisplayName, EMPTY_STRING)
                    .addProperty(NETWORKADAPTER_IS_UP,
                        NetworkInterface::isUp, false)
                    .addProperty(NETWORKADAPTER_IS_LOOPBACK,
                        NetworkInterface::isLoopback, false)
                    .addProperty(NETWORKADAPTER_IS_POINTTOPOINT,
                        NetworkInterface::isPointToPoint, false)
                    .addProperty(NETWORKADAPTER_IS_VIRTUAL,
                        NetworkInterface::isVirtual, false)
                    .addProperty(NETWORKADAPTER_SUPPORTS_MULTICAST,
                        NetworkInterface::supportsMulticast, false)
                    .addProperty(NETWORKADAPTER_PARENT,
                        t -> ofNullable(t.getParent())
                            .map(Utils::pid)
                            .orElse(EMPTY_STRING))
                    .addProperty(NETWORKADAPTER_SUBINTERFACE,
                        t -> Collections.list(t.getSubInterfaces())
                            .stream()
                            .map(Utils::pid)
                            .toArray(String[]::new))
                    .addProperty(SERVICE_PID, Utils::pid)
                    .build()
            )
            .withNewInstanceNotificationConsumer((i, a) -> {
                RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl> interfaceAddressManager = createInterfaceAddressManager(i, addressRegistrationFunction);
                interfaceAddressRegistrationManagers.put(a.getId(), interfaceAddressManager);
                interfaceAddressManager.updateServices(i.getInterfaceAddresses());
            })
            .withRemoveInstanceNotificationConsumer(a -> {
                RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl> interfaceAddressManager = interfaceAddressRegistrationManagers.remove(a.getId());
                interfaceAddressManager.updateServices(Collections.emptyList());
            })
            .withUpdateInstanceNotificationConsumer(a -> {
                RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl> interfaceAddressManager = interfaceAddressRegistrationManagers.get(a.getId());
                interfaceAddressManager.updateServices(a.getInterfaceAddresses());
            })
            .build();
    }

    private RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl> createInterfaceAddressManager(
        NetworkInterface networkInterface,
        Functions.TriFunction<Class<NetworkAddress>, NetworkAddressImpl, Dictionary, ServiceRegistration<NetworkAddress>> registrationTriFunction) {
        return new RegistrationManager.Builder<InterfaceAddress, NetworkAddress, NetworkAddressImpl>(NetworkAddress.class)
            .withRegistrationFunction(registrationTriFunction)
            .withIdFunction(t -> t.getAddress().getHostAddress())
            .withNewInstanceFunction(NetworkAddressImpl::new)
            .withPropertiesCollector(
                new PropertiesCollector.Builder<InterfaceAddress>()
                    .addProperty(NetworkAddress.NETWORKADAPTER_TYPE,
                        t -> ipRegistry.getType(getAddressForRegistry(networkInterface)))
                    .addProperty(IPADDRESS_VERSION,
                        t -> isIPv4(t.getAddress()) ? IPADDRESS_VERSION_4 : IPADDRESS_VERSION_6)
                    .addProperty(IPADDRESS_SCOPE, t -> ipRegistry.getScope(t.getAddress()))
                    .addProperty(IPADDRESS, t -> t.getAddress().getHostAddress())
                    .addProperty(SUBNETMASK_LENGTH, InterfaceAddress::getNetworkPrefixLength)
                    .addProperty(NETWORKADAPTER_PID,t -> Utils.pid(networkInterface))
                    .addProperty(SERVICE_PID, Utils::pid)
                    .build()
            )
            .build();
    }
}
