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

package org.knowhowlab.osgi.niis.impl;

import org.osgi.service.networkadapter.NetworkAddress;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.util.function.Supplier;

/**
 * @author dpishchukhin
 */
public class NetworkAddressImpl implements NetworkAddress {
    private InterfaceAddress address;
    private final Supplier<String> networkAdapterTypeSupplier;
    private final Supplier<String> networkAdapterPidSupplier;
    private final Supplier<String> scopeSupplier;

    public NetworkAddressImpl(InterfaceAddress address,
                              Supplier<String> networkAdapterTypeSupplier,
                              Supplier<String> networkAdapterPidSupplier,
                              Supplier<String> scopeSupplier) {
        this.address = address;
        this.networkAdapterTypeSupplier = networkAdapterTypeSupplier;
        this.networkAdapterPidSupplier = networkAdapterPidSupplier;
        this.scopeSupplier = scopeSupplier;
    }

    @Override
    public String getNetworkAdapterType() {
        return networkAdapterTypeSupplier.get();
    }

    @Override
    public String getIpAddressVersion() {
        return address.getAddress().getAddress().length == 4 ?
            NetworkAddress.IPADDRESS_VERSION_4 :
            NetworkAddress.IPADDRESS_VERSION_6;
    }

    @Override
    public String getIpAddressScope() {
        return scopeSupplier.get();
    }

    @Override
    public String getIpAddress() {
        return address.getAddress().getHostName();
    }

    @Override
    public InetAddress getInetAddress() {
        return address.getAddress();
    }

    @Override
    public int getSubnetMaskLength() {
        return address.getNetworkPrefixLength();
    }

    @Override
    public String getNetworkAdapterPid() {
        return networkAdapterPidSupplier.get();
    }
}
