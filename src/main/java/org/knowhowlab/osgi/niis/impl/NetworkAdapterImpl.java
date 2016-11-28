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
import java.util.Map;

import static org.knowhowlab.osgi.niis.utils.Functions.cast;

/**
 * @author dpishchukhin
 */
public class NetworkAdapterImpl extends AbstractInstance<NetworkInterface> implements NetworkAdapter {
    public NetworkAdapterImpl(NetworkInterface networkInterface, Map<String, Object> properties) {
        super(networkInterface, properties);
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public String getNetworkAdapterType() {
        return cast(String.class::cast, properties.get(NETWORKADAPTER_TYPE))
            .orElse(EMPTY_STRING);
    }

    @Override
    public String getDisplayName() {
        return cast(String.class::cast, properties.get(NETWORKADAPTER_DISPLAYNAME))
            .orElse(NetworkAdapter.EMPTY_STRING);
    }

    @Override
    public String getName() {
        return cast(String.class::cast, properties.get(NETWORKADAPTER_NAME))
            .orElse(NetworkAdapter.EMPTY_STRING);
    }

    @Override
    public byte[] getHardwareAddress() {
        return cast(byte[].class::cast, properties.get(NETWORKADAPTER_SUPPORTS_MULTICAST))
            .orElse(NetworkAdapter.EMPTY_BYTE_ARRAY);
    }

    @Override
    public int getMTU() throws SocketException {
        return source.getMTU();
    }

    @Override
    public boolean isLoopback() throws SocketException {
        return cast(boolean.class::cast, properties.get(NETWORKADAPTER_IS_LOOPBACK))
            .orElse(false);
    }

    @Override
    public boolean isPointToPoint() throws SocketException {
        return cast(boolean.class::cast, properties.get(NETWORKADAPTER_IS_POINTTOPOINT))
            .orElse(false);
    }

    @Override
    public boolean isUp() throws SocketException {
        return cast(boolean.class::cast, properties.get(NETWORKADAPTER_IS_UP))
            .orElse(false);
    }

    @Override
    public boolean isVirtual() {
        return cast(boolean.class::cast, properties.get(NETWORKADAPTER_IS_VIRTUAL))
            .orElse(false);
    }

    @Override
    public boolean supportsMulticast() throws SocketException {
        return cast(boolean.class::cast, properties.get(NETWORKADAPTER_SUPPORTS_MULTICAST))
            .orElse(false);
    }
}
