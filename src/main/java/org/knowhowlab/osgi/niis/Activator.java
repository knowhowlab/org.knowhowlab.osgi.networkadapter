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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static org.knowhowlab.osgi.niis.utils.CIDR.isIPv4;
import static org.knowhowlab.osgi.niis.utils.Functions.ofThrowable;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.networkadapter.NetworkAdapter.*;
import static org.osgi.service.networkadapter.NetworkAddress.*;

/**
 * @author dpishchukhin
 */
public class Activator implements BundleActivator {
    private static final String REFRESH_DELAY_PROPERTY = "org.knowhowlab.osgi.niis.refresh_delay";

    private static final String REFRESH_DELAY_DEFAULT = String.valueOf(TimeUnit.SECONDS.toMillis(5));

    private ScheduledExecutorService pool;

    private RegistrationManager<NetworkInterface, NetworkAdapter, NetworkAdapterImpl> networkInterfaceRegistrationManager;
    private RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl> interfaceAddressRegistrationManager;
    private IPRegistry ipRegistry;

    @Override
    public void start(BundleContext bc) throws Exception {
        networkInterfaceRegistrationManager = createNetworkInterfaceManager(bc::registerService);
        interfaceAddressRegistrationManager = createInterfaceAddressManager(bc::registerService);

        ipRegistry = IPRegistry.rfc6890();

        // read props
        long refreshDelay = parseLong(ofNullable(bc.getProperty(REFRESH_DELAY_PROPERTY)).orElse(REFRESH_DELAY_DEFAULT));

        pool = Executors.newScheduledThreadPool(0);
        pool.scheduleWithFixedDelay(new NetworkMonitor(), 0, refreshDelay, TimeUnit.MILLISECONDS);
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
                        t -> ipRegistry.getType(t.getInterfaceAddresses().stream()
                            .filter(a -> isIPv4(a.getAddress()))
                            .findFirst().orElse(t.getInterfaceAddresses().get(0)).getAddress()))
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
                            .map(NetworkInterface::getName)
                            .orElse(EMPTY_STRING))
                    .addProperty(NETWORKADAPTER_SUBINTERFACE,
                        t -> Collections.list(t.getSubInterfaces())
                            .stream()
                            .map(NetworkInterface::getName)
                            .toArray(String[]::new))
                    .addProperty(SERVICE_PID, NetworkInterface::getName)
                    .build()
            )
            .build();
    }

    private RegistrationManager<InterfaceAddress, NetworkAddress, NetworkAddressImpl> createInterfaceAddressManager(
        Functions.TriFunction<Class<NetworkAddress>, NetworkAddressImpl, Dictionary, ServiceRegistration<NetworkAddress>> registrationTriFunction) {
        return new RegistrationManager.Builder<InterfaceAddress, NetworkAddress, NetworkAddressImpl>(NetworkAddress.class)
            .withRegistrationFunction(registrationTriFunction)
            .withIdFunction(t -> t.getAddress().getHostAddress())
            .withNewInstanceFunction(NetworkAddressImpl::new)
            .withPropertiesCollector(
                new PropertiesCollector.Builder<InterfaceAddress>()
                    .addProperty(NetworkAddress.NETWORKADAPTER_TYPE, t -> ipRegistry.getType(t.getAddress())) // FIXME: take from NA
                    .addProperty(IPADDRESS_VERSION,
                        t -> isIPv4(t.getAddress()) ? IPADDRESS_VERSION_4 : IPADDRESS_VERSION_6)
                    .addProperty(IPADDRESS_SCOPE, t -> ipRegistry.getScope(t.getAddress()))
                    .addProperty(IPADDRESS, t -> t.getAddress().getHostAddress())
                    .addProperty(SUBNETMASK_LENGTH, InterfaceAddress::getNetworkPrefixLength)
                    .addProperty(NETWORKADAPTER_PID,
                        t -> ofThrowable(NetworkInterface::getByInetAddress, t::getAddress)
                            .map(NetworkInterface::getName)
                            .orElse(EMPTY_STRING))
                    .addProperty(SERVICE_PID, t -> t.getAddress().getHostAddress())
                    .build()
            )
            .build();
    }

    @Override
    public void stop(BundleContext bc) throws Exception {
        interfaceAddressRegistrationManager.close();
        networkInterfaceRegistrationManager.close();
        pool.shutdownNow();
    }

    private class NetworkMonitor implements Runnable {
        @Override
        public void run() {
            List<NetworkInterface> networkInterfaces = Collections.list(ofThrowable(NetworkInterface::getNetworkInterfaces)
                .orElseGet(Collections::emptyEnumeration));
            List<InterfaceAddress> interfaceAddresses = networkInterfaces.stream()
                .flatMap(n -> n.getInterfaceAddresses().stream())
                .collect(Collectors.toList());

            // todo: refactor: register address then adapter. unregister adapter then address
            interfaceAddressRegistrationManager.updateServices(interfaceAddresses);
            networkInterfaceRegistrationManager.updateServices(networkInterfaces);
        }
    }
}
