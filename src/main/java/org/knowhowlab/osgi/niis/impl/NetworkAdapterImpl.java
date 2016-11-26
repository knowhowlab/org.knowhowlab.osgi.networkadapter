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

import org.osgi.service.networkadapter.NetworkAdapter;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.function.Supplier;

import static org.knowhowlab.osgi.niis.utils.Functions.ofThrowable;

/**
 * @author dpishchukhin
 */
public class NetworkAdapterImpl implements NetworkAdapter {
    private NetworkInterface networkInterface;
    private Supplier<String> typeSupplier;

    public NetworkAdapterImpl(NetworkInterface networkInterface, Supplier<String> typeSupplier) {
        this.networkInterface = networkInterface;
        this.typeSupplier = typeSupplier;
    }

    @Override
    public String getNetworkAdapterType() {
        return typeSupplier.get();
    }

    @Override
    public String getDisplayName() {
        return networkInterface.getDisplayName();
    }

    @Override
    public String getName() {
        return networkInterface.getName();
    }

    @Override
    public byte[] getHardwareAddress() {
        return ofThrowable(networkInterface::getHardwareAddress).orElse(EMPTY_BYTE_ARRAY);
    }

    @Override
    public int getMTU() throws SocketException {
        return networkInterface.getMTU();
    }

    @Override
    public boolean isLoopback() throws SocketException {
        return networkInterface.isLoopback();
    }

    @Override
    public boolean isPointToPoint() throws SocketException {
        return networkInterface.isPointToPoint();
    }

    @Override
    public boolean isUp() throws SocketException {
        return networkInterface.isUp();
    }

    @Override
    public boolean isVirtual() {
        return networkInterface.isVirtual();
    }

    @Override
    public boolean supportsMulticast() throws SocketException {
        return networkInterface.supportsMulticast();
    }
}
