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

import org.knowhowlab.osgi.networkadapter.utils.CIDR;

import java.net.InetAddress;
import java.util.*;

import static org.knowhowlab.osgi.networkadapter.utils.CIDR.of;
import static org.osgi.service.networkadapter.NetworkAdapter.LAN;
import static org.osgi.service.networkadapter.NetworkAdapter.WAN;
import static org.osgi.service.networkadapter.NetworkAddress.*;

/**
 * @author dpishchukhin
 */
public class IPRegistry {
    public static final String IPADDRESS_SCHOPE_OTHER = "OTHER";
    public static final String OTHER = "OTHER";

    private Map<String, List<CIDR>> types;
    private Map<String, List<CIDR>> scopes;
    private String defaultType;
    private String defaultScope;

    public IPRegistry(Map<String, List<CIDR>> types, Map<String, List<CIDR>> scopes, 
                      String defaultType, String defaultScope) {
        this.types = types;
        this.scopes = scopes;
        this.defaultType = defaultType;
        this.defaultScope = defaultScope;
    }

    public String getType(InetAddress inetAddress) {
        return findKey(types, inetAddress, defaultType);
    }

    public String getScope(InetAddress inetAddress) {
        return findKey(scopes, inetAddress, defaultScope);
    }

    private String findKey(Map<String, List<CIDR>> map, InetAddress inetAddress, String defaultValue) {
        return map.entrySet()
            .stream()
            .flatMap(entry -> entry.getValue()
                .stream()
                .map(cidr -> new AbstractMap.SimpleEntry<>(entry.getKey(), cidr)))
            .filter(entry -> entry.getValue().contains(inetAddress))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(defaultValue);
    }

    public static class Builder {
        private Map<String, List<CIDR>> types = new HashMap<>();
        private Map<String, List<CIDR>> scopes = new HashMap<>();
        private String defaultType;
        private String defaultScope;

        public Builder entry(String type, String scope, String cidr) {
            addKey(types, type, of(cidr));
            addKey(scopes, scope, of(cidr));
            return this;
        }

        public Builder defaultScope(String defaultScope) {
            this.defaultScope = defaultScope;
            return this;
        }

        public Builder defaultType(String defaultType) {
            this.defaultType = defaultType;
            return this;
        }

        private void addKey(Map<String, List<CIDR>> map, String key, CIDR cidr) {
            map.computeIfAbsent(key, k -> new ArrayList<>(Collections.singletonList(cidr)));
            map.computeIfPresent(key, (k, l) -> {
                l.add(cidr);
                return l;
            });
        }

        public IPRegistry build() {
            return new IPRegistry(types, scopes, defaultType, defaultScope);
        }
    }

    public static IPRegistry rfc6890() {
        return new Builder()
            .defaultType(WAN)
            .defaultScope(IPADDRESS_SCOPE_GLOBAL)

            // PRIVATE_USE
            .entry(LAN, IPADDRESS_SCOPE_PRIVATE_USE, "10.0.0.0/8") // Table 2: Private-Use Networks
            .entry(LAN, IPADDRESS_SCOPE_PRIVATE_USE, "172.16.0.0/12") // Table 6: Private-Use Networks
            .entry(LAN, IPADDRESS_SCOPE_PRIVATE_USE, "192.168.0.0/16") // Table 11: Private-Use Networks
            // LOOPBACK
            .entry(OTHER, IPADDRESS_SCOPE_LOOPBACK, "127.0.0.0/8") // Table 4: Loopback
            .entry(OTHER, IPADDRESS_SCOPE_LOOPBACK, "::1/128") // Table 17: Loopback Address
            // LINKLOCAL
            .entry(LAN, IPADDRESS_SCOPE_LINKLOCAL, "169.254.0.0/16") // Table 5: Link Local
            // UNIQUE_LOCAL
            .entry(OTHER, IPADDRESS_SCOPE_UNIQUE_LOCAL, "fc00::/7") // Table 28: Unique-Local
            // UNSPECIFIED
            .entry(OTHER, IPADDRESS_SCOPE_UNSPECIFIED, "::/128") // Table 18: Unspecified Address
            // HOST
            .entry(OTHER, IPADDRESS_SCOPE_HOST, "0.0.0.0/8") // Table 1: "This host on this network"
            // SHARED
            .entry(OTHER, IPADDRESS_SCOPE_SHARED, "100.64.0.0/10") // Table 3: Shared Address Space
            // LINKED_SCOPED_UNICAST
            .entry(OTHER, IPADDRESS_SCOPE_LINKED_SCOPED_UNICAST, "fe80::/10") // Table 29: Linked-Scoped Unicast
            // Other scopes
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "192.0.0.0/24") // Table 7: IETF Protocol Assignments
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "192.0.0.0/29") // Table 8: DS-Lite
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "192.0.2.0/24") // Table 9: TEST-NET-1
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "192.88.99.0/24") // Table 10: 6to4 Relay Anycast
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "198.18.0.0/15") // Table 12: Network Interconnect Device Benchmark Testing
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "198.51.100.0/24") // Table 13: TEST-NET-2
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "203.0.113.0/24") // Table 14: TEST-NET-3
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "240.0.0.0/4") // Table 15: Reserved for Future Use
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "255.255.255.255/32") // Table 16: Limited Broadcast
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "64:ff9b::/96") // Table 19: IPv4-IPv6 Translation Address
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "::ffff:0:0/96") // Table 20: IPv4-Mapped Address
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "100::/64") // Table 21: Discard-Only Prefix
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "2001::/23") // Table 22: IETF Protocol Assignments
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "2001::/32") // Table 23: TEREDO
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "2001:2::/48") // Table 24: Benchmarking
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "2001:db8::/32") // Table 25: Documentation
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "2001:10::/28") // Table 26: ORCHID
            .entry(OTHER, IPADDRESS_SCHOPE_OTHER, "2002::/16") // Table 27: 6to4

            .build();
    }
}

