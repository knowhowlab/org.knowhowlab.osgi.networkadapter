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

package org.knowhowlab.osgi.networkadapter.registry;

import org.junit.Test;

import java.net.InetAddress;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author dpishchukhin
 */
public class IPRegistryTest {
    @Test
    public void customRegistry() throws Exception {
        IPRegistry registry = new IPRegistry.Builder()
            .defaultType("DEF_TYPE")
            .defaultScope("DEF_SCOPE")
            .entry("TEST_TYPE", "TEST_SCOPE", "10.0.0.0/8")
            .build();

        assertThat(registry, notNullValue());
        assertThat(registry.getType(InetAddress.getByName("192.168.0.1")), is("DEF_TYPE"));
        assertThat(registry.getScope(InetAddress.getByName("192.168.0.1")), is("DEF_SCOPE"));
        assertThat(registry.getType(InetAddress.getByName("10.0.1.1")), is("TEST_TYPE"));
        assertThat(registry.getScope(InetAddress.getByName("10.0.1.1")), is("TEST_SCOPE"));
    }
}
