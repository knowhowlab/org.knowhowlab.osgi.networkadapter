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

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Constants;
import org.osgi.service.networkadapter.NetworkAdapter;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author dpishchukhin
 */
public class NetworkPropertiesTest {
    @Test
    public void read_NetworkInterface_onlyOne() throws Exception {
        NetworkInterface networkInterface = createNetworkInterface(() -> "eth0");
        Hashtable<String, Object> properties = NetworkProperties.read(networkInterface);

        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_NAME), is("eth0"));
        Assert.assertThat(properties.get(Constants.SERVICE_PID), is("eth0"));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_TYPE), is(NetworkAdapter.LAN)); //TODO
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS), is(new byte[]{1, 2, 3, 4}));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME), is("ETH - 0"));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_IS_UP), is(true));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK), is(true));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT), is(true));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST), is(true));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_PARENT), is(NetworkAdapter.EMPTY_STRING));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE), is(NetworkAdapter.EMPTY_STRING_ARRAY));
    }

    @Test
    public void read_NetworkInterface_withParent() throws Exception {
        NetworkInterface networkInterface0 = createNetworkInterface(() -> "eth0");
        NetworkInterface networkInterface = createNetworkInterface(
            () -> "eth1",
            () -> networkInterface0,
            Collections::emptyEnumeration);
        Hashtable<String, Object> properties = NetworkProperties.read(networkInterface);

        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_NAME), is("eth1"));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_PARENT), is("eth0"));
    }

    @Test
    public void read_NetworkInterface_withSubs() throws Exception {
        NetworkInterface networkInterface1 = createNetworkInterface(() -> "eth1");
        NetworkInterface networkInterface2 = createNetworkInterface(() -> "eth2");
        NetworkInterface networkInterface = createNetworkInterface(
            () -> "eth0",
            () -> null,
            () -> Collections.enumeration(Arrays.asList(
                networkInterface1,
                networkInterface2
                )
            ));
        Hashtable<String, Object> properties = NetworkProperties.read(networkInterface);

        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_NAME), is("eth0"));
        Assert.assertThat(properties.get(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE), is(new String[]{"eth1", "eth2"}));
    }

    private NetworkInterface createNetworkInterface(
        Supplier<String> name,
        Supplier<byte[]> hardwareAddress,
        Supplier<String> displayName,
        Supplier<Boolean> isUp,
        Supplier<Boolean> isLoopback,
        Supplier<Boolean> isPointToPoint,
        Supplier<Boolean> isVirtual,
        Supplier<Boolean> supportMulticast,
        Supplier<NetworkInterface> parent,
        Supplier<Enumeration<NetworkInterface>> subs
    ) {
        NetworkInterface mock = mock(NetworkInterface.class);

        when(mock.getName()).thenReturn(name.get());
        try {
            when(mock.getHardwareAddress()).thenReturn(hardwareAddress.get());
            when(mock.getDisplayName()).thenReturn(displayName.get());
            when(mock.isUp()).thenReturn(isUp.get());
            when(mock.isLoopback()).thenReturn(isLoopback.get());
            when(mock.isPointToPoint()).thenReturn(isPointToPoint.get());
            when(mock.isVirtual()).thenReturn(isVirtual.get());
            when(mock.supportsMulticast()).thenReturn(supportMulticast.get());
        } catch (SocketException e) {
            e.printStackTrace();
        }

        when(mock.getParent()).thenReturn(parent.get());
        when(mock.getSubInterfaces()).thenReturn(subs.get());
        return mock;
    }

    private NetworkInterface createNetworkInterface(
        Supplier<String> name,
        Supplier<NetworkInterface> parent,
        Supplier<Enumeration<NetworkInterface>> subs
    ) {
        return createNetworkInterface(
            name,
            () -> new byte[]{1, 2, 3, 4},
            () -> "ETH - 0",
            () -> true,
            () -> true,
            () -> true,
            () -> true,
            () -> true,
            parent,
            subs);
    }

    private NetworkInterface createNetworkInterface(
        Supplier<String> name
    ) {
        return createNetworkInterface(
            name,
            () -> new byte[]{1, 2, 3, 4},
            () -> "ETH - 0",
            () -> true,
            () -> true,
            () -> true,
            () -> true,
            () -> true,
            () -> null,
            Collections::emptyEnumeration);
    }
}
