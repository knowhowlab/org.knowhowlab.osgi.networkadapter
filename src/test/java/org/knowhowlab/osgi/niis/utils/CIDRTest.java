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

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author dpishchukhin
 */
public class CIDRTest {
    @Test(expected = NullPointerException.class)
    public void of_NPE() throws Exception {
        CIDR.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_IAE() throws Exception {
        CIDR.of("192.168.0.1");
    }

    @Test(expected = UnknownHostException.class)
    public void of_UHE() throws Exception {
        CIDR.of("aaa.bbb.ccc.ddd/eee");
    }

    @Test
    public void of_ip4() throws Exception {
        CIDR cidr = CIDR.of("192.168.0.0/24");
        assertThat(cidr, notNullValue());
        assertThat(cidr.getNetworkAddress(), is(InetAddress.getByName("192.168.0.0")));
        assertThat(cidr.getBroadcastAddress(), is(InetAddress.getByName("192.168.0.255")));
    }

    @Test
    public void of_ip4_another() throws Exception {
        CIDR cidr = CIDR.of("192.168.0.1/24");
        assertThat(cidr, notNullValue());
        assertThat(cidr.getNetworkAddress(), is(InetAddress.getByName("192.168.0.0")));
        assertThat(cidr.getBroadcastAddress(), is(InetAddress.getByName("192.168.0.255")));
    }

    @Test
    public void of_ip6() throws Exception {
        CIDR cidr = CIDR.of("::1/64");
        assertThat(cidr, notNullValue());
        assertThat(cidr.getNetworkAddress(), is(InetAddress.getByName("0:0:0:0:0:0:0:0")));
        assertThat(cidr.getBroadcastAddress(), is(InetAddress.getByName("0:0:0:0:ffff:ffff:ffff:ffff")));
    }

    @Test
    public void of_ip6_another() throws Exception {
        CIDR cidr = CIDR.of("::10/64");
        assertThat(cidr, notNullValue());
        assertThat(cidr.getNetworkAddress(), is(InetAddress.getByName("0:0:0:0:0:0:0:0")));
        assertThat(cidr.getBroadcastAddress(), is(InetAddress.getByName("0:0:0:0:ffff:ffff:ffff:ffff")));
    }

    @Test
    public void contains_ip4() throws Exception {
        CIDR cidr = CIDR.of("192.168.0.1/24");
        assertThat(cidr.contains(InetAddress.getByName("192.168.0.10")), is(true));
        assertThat(cidr.contains(InetAddress.getByName("192.168.1.10")), is(false));
    }

    @Test
    public void contains_ip6() throws Exception {
        CIDR cidr = CIDR.of("::1/127");
        assertThat(cidr.contains(InetAddress.getByName("0:0:0:0:0:0:0:0")), is(true));
        assertThat(cidr.contains(InetAddress.getByName("0:0:0:0:0:0:1:0")), is(false));
    }
}
