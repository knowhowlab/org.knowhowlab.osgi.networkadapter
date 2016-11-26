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

import org.osgi.framework.Constants;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Hashtable;

import static java.util.Optional.ofNullable;
import static org.knowhowlab.osgi.niis.utils.Functions.ofThrowable;

/**
 * @author dpishchukhin
 */
public class NetworkProperties {
    private NetworkProperties() {
    }

    public static Hashtable<String, Object> read(NetworkInterface networkInterface) {
        Hashtable<String, Object> properties = new Hashtable<>();

        properties.put(NetworkAdapter.NETWORKADAPTER_TYPE, NetworkAdapter.LAN); // TODO

        properties.put(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS,
            ofThrowable(networkInterface::getHardwareAddress)
                .orElse(NetworkAdapter.EMPTY_BYTE_ARRAY));
        properties.put(NetworkAdapter.NETWORKADAPTER_NAME,
            networkInterface.getName());
        properties.put(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME,
            ofNullable(networkInterface.getDisplayName()).orElse(NetworkAdapter.EMPTY_STRING));
        properties.put(NetworkAdapter.NETWORKADAPTER_IS_UP,
            ofThrowable(networkInterface::isUp)
                .orElse(false));
        properties.put(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK,
            ofThrowable(networkInterface::isLoopback)
                .orElse(false));
        properties.put(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT,
            ofThrowable(networkInterface::isPointToPoint)
                .orElse(false));
        properties.put(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL,
            networkInterface.isVirtual());
        properties.put(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST,
            ofThrowable(networkInterface::supportsMulticast)
                .orElse(false));
        properties.put(NetworkAdapter.NETWORKADAPTER_PARENT,
            ofNullable(networkInterface.getParent())
                .map(NetworkInterface::getName)
                .orElse(NetworkAdapter.EMPTY_STRING)
        );
        properties.put(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE,
            Collections.list(networkInterface.getSubInterfaces())
                .stream()
                .map(NetworkInterface::getName)
                .toArray(String[]::new)
        );

        properties.put(Constants.SERVICE_PID, networkInterface.getName());

        return properties;
    }

    public static Hashtable<String, Object> read(InterfaceAddress interfaceAddress, NetworkAdapter parent) {
        Hashtable<String, Object> properties = new Hashtable<>();

        properties.put(NetworkAddress.NETWORKADAPTER_TYPE, parent.getNetworkAdapterType());

        InetAddress address = interfaceAddress.getAddress();

        properties.put(NetworkAddress.IPADDRESS_VERSION,
            address.getAddress().length == 4 ?
                NetworkAddress.IPADDRESS_VERSION_4 :
                NetworkAddress.IPADDRESS_VERSION_6);

        properties.put(NetworkAddress.IPADDRESS_SCOPE, NetworkAddress.IPADDRESS_SCOPE_GLOBAL); // TODO
        properties.put(NetworkAddress.IPADDRESS, address.getHostAddress());
        properties.put(NetworkAddress.SUBNETMASK_LENGTH, interfaceAddress.getNetworkPrefixLength());

        properties.put(NetworkAddress.NETWORKADAPTER_PID, parent.getName());

        properties.put(Constants.SERVICE_PID, address.getHostAddress());

        return properties;
    }
}
