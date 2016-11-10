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

package org.knowhowlab.osgi.networkadapter.impl;

import org.osgi.service.networkadapter.NetworkAddress;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.Map;

import static org.knowhowlab.osgi.networkadapter.utils.Functions.cast;
import static org.osgi.service.networkadapter.NetworkAdapter.EMPTY_STRING;

/**
 * @author dpishchukhin
 */
public class NetworkAddressImpl extends AbstractInstance<InterfaceAddress> implements NetworkAddress {
    public NetworkAddressImpl(InterfaceAddress networkInterface, Map<String, Object> properties) {
        super(networkInterface, properties);
    }

    @Override
    public String getId() {
        return getIpAddress();
    }

    @Override
    public String getNetworkAdapterType() {
        return cast(String.class::cast, properties.get(NETWORKADAPTER_TYPE))
            .orElse(EMPTY_STRING);
    }

    @Override
    public String getIpAddressVersion() {
        return cast(String.class::cast, properties.get(IPADDRESS_VERSION))
            .orElse(EMPTY_STRING);
    }

    @Override
    public String getIpAddressScope() {
        return cast(String.class::cast, properties.get(IPADDRESS_SCOPE))
            .orElse(EMPTY_STRING);
    }

    @Override
    public String getIpAddress() {
        return cast(String.class::cast, properties.get(IPADDRESS))
            .orElse(EMPTY_STRING);
    }

    @Override
    public InetAddress getInetAddress() {
        return source.getAddress();
    }

    @Override
    public int getSubnetMaskLength() {
        return cast(int.class::cast, properties.get(SUBNETMASK_LENGTH))
            .orElse(EMPTY_INTEGER);
    }

    @Override
    public String getNetworkAdapterPid() {
        return cast(String.class::cast, properties.get(NETWORKADAPTER_PID))
            .orElse(EMPTY_STRING);
    }
}
