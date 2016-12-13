org.knowhowlab.osgi.networkadapter
========================

### 143. Network Interface Information Service Specification Implementation. 

[Javadoc](https://osgi.org/javadoc/r6/residential/org/osgi/service/networkadapter/package-summary.html)

[![Build Status](https://travis-ci.org/knowhowlab/org.knowhowlab.osgi.networkadapter.svg?branch=master)](https://travis-ci.org/knowhowlab/org.knowhowlab.osgi.networkadapter)
[![Coverage Status](https://coveralls.io/repos/github/knowhowlab/org.knowhowlab.osgi.networkadapter/badge.svg?branch=master)](https://coveralls.io/github/knowhowlab/org.knowhowlab.osgi.networkadapter?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.knowhowlab.osgi/networkadapter/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/org.knowhowlab.osgi/networkadapter/)

## Release Notes

# 0.1 (13.12.2016)

- requires Java 8+
- requires OSGi 6+
- exports package org.osgi.service.networkadapter;version="1.0"

## IP Address Registries (RFC6809)

| Address Block      | Network Adapter Type | IP Address Scope      | RFC Table                                               |
|--------------------|----------------------|-----------------------|---------------------------------------------------------|
| 0.0.0.0/8          | OTHER                | HOST                  | Table 1: "This host on this network"                    |
| 10.0.0.0/8         | LAN                  | PRIVATE_USE           | Table 2: Private-Use Networks                           |
| 100.64.0.0/10      | OTHER                | SHARED                | Table 3: Shared Address Space                           |
| 127.0.0.0/8        | OTHER                | LOOPBACK              | Table 4: Loopback                                       |
| 169.254.0.0/16     | LAN                  | LINKLOCAL             | Table 5: Link Local                                     |
| 172.16.0.0/12      | LAN                  | PRIVATE_USE           | Table 6: Private-Use Networks                           |
| 192.0.0.0/24       | OTHER                | OTHER                 | Table 7: IETF Protocol Assignments                      |
| 192.0.0.0/29       | OTHER                | OTHER                 | Table 8: DS-Lite                                        |
| 192.0.2.0/24       | OTHER                | OTHER                 | Table 9: TEST-NET-1                                     |
| 192.88.99.0/24     | OTHER                | OTHER                 | Table 10: 6to4 Relay Anycast                            |
| 192.168.0.0/16     | LAN                  | PRIVATE_USE           | Table 11: Private-Use Networks                          |
| 198.18.0.0/15      | OTHER                | OTHER                 | Table 12: Network Interconnect Device Benchmark Testing |
| 198.51.100.0/24    | OTHER                | OTHER                 | Table 13: TEST-NET-2                                    |
| 203.0.113.0/24     | OTHER                | OTHER                 | Table 14: TEST-NET-3                                    |
| 240.0.0.0/4        | OTHER                | OTHER                 | Table 15: Reserved for Future Use                       |
| 255.255.255.255/32 | OTHER                | OTHER                 | Table 16: Limited Broadcast                             |
| ::1/128            | OTHER                | LOOPBACK              | Table 17: Loopback Address                              |
| ::/128             | OTHER                | UNSPECIFIED           | Table 18: Unspecified Address                           |
| 64:ff9b::/96       | OTHER                | OTHER                 | Table 19: IPv4-IPv6 Translation Address                 |
| ::ffff:0:0/96      | OTHER                | OTHER                 | Table 20: IPv4-Mapped Address                           |
| 100::/64           | OTHER                | OTHER                 | Table 21: Discard-Only Prefix                           |
| 2001::/23          | OTHER                | OTHER                 | Table 22: IETF Protocol Assignments                     |
| 2001::/32          | OTHER                | OTHER                 | Table 23: TEREDO                                        |
| 2001:2::/48        | OTHER                | OTHER                 | Table 24: Benchmarking                                  |
| 2001:db8::/32      | OTHER                | OTHER                 | Table 25: Documentation                                 |
| 2001:10::/28       | OTHER                | OTHER                 | Table 26: ORCHID                                        |
| 2002::/16          | OTHER                | OTHER                 | Table 27: 6to4                                          |
| fc00::/7           | OTHER                | UNIQUE_LOCAL          | Table 28: Unique-Local                                  |
| fe80::/10          | OTHER                | LINKED_SCOPED_UNICAST | Table 29: Linked-Scoped Unicast                         |
| *                  | WAN                  | GLOBAL                |                                                         |
