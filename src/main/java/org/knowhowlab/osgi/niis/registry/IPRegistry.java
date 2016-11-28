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

package org.knowhowlab.osgi.niis.registry;

import java.net.InetAddress;
import java.util.Map;

/**
 * @author dpishchukhin
 */
public class IPRegistry {
    public static final String LAN_IP4_PRIVATE_ADDRESSES_DEFAULT = "10.0.0.0/8,172.16.0.0/12,192.168.0.0/16";
    public static final String LAN_IP6_PRIVATE_ADDRESSES_DEFAULT = "fc00::/7";
    public static final String LAN_IP4_LOCAL_LINK_ADDRESSES_DEFAULT = "169.254.0.0/16";
    public static final String LAN_IP6_LOCAL_LINK_ADDRESSES_DEFAULT = "fe80::/10";

    private Map<String, Rule<InetAddress>> types;
    private Map<String, Rule<InetAddress>> scopes;

    private IPRegistry() {
    }

    public String getType(InetAddress inetAddress) {
        return "LAN";
    }

    public String getScope(InetAddress inetAddress) {
        return "GLOBAL";
    }

    public static class Builder {
        public static IPRegistry rfc6890() {
            // todo: add default rules
            return new IPRegistry();
        }
    }

    private class Rule<T> {
    }
}
