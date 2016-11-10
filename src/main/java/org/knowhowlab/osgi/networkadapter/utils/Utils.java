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

package org.knowhowlab.osgi.networkadapter.utils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;

/**
 * @author dpishchukhin
 */
public class Utils {
    public static boolean isIPv4(InetAddress inetAddress) {
        return inetAddress.getAddress().length == 4;
    }

    public static String pid(NetworkInterface networkInterface) {
        return networkInterface.getName();
    }

    public static String pid(InterfaceAddress interfaceAddress) {
        return interfaceAddress.getAddress().getHostAddress();
    }

    public static InetAddress getAddressForRegistry(NetworkInterface t) {
        return t.getInterfaceAddresses().stream()
            .filter(a -> isIPv4(a.getAddress()))
            .findFirst().orElse(t.getInterfaceAddresses().get(0)).getAddress();
    }
}
