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

import org.osgi.service.networkadapter.NetworkAdapter;

import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * @author dpishchukhin
 */
class NetworkAdapterImpl implements NetworkAdapter {
    private NetworkInterface networkInterface;

    NetworkAdapterImpl(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
    }

    @Override
    public String getNetworkAdapterType() {
        return null;
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
        try {
            return networkInterface.getHardwareAddress();
        } catch (SocketException e) {
            return EMPTY_BYTE_ARRAY;
        }
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
