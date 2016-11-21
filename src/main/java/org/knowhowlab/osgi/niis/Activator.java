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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.networkadapter.NetworkAdapter;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;

/**
 * @author dpishchukhin
 */
public class Activator implements BundleActivator {
    public static final String REFRESH_DELAY_PROPERTY = "org.knowhowlab.osgi.niis.refresh_delay";
    public static final String LAN_IP4_PRIVATE_ADDRESSES_PROPERTY = "org.knowhowlab.osgi.niis.lan_ip4_private_addresses";
    public static final String LAN_IP6_PRIVATE_ADDRESSES_PROPERTY = "org.knowhowlab.osgi.niis.lan_ip6_private_addresses";
    public static final String LAN_IP4_LOCAL_LINK_ADDRESSES_PROPERTY = "org.knowhowlab.osgi.niis.lan_ip4_local_link_addresses";
    public static final String LAN_IP6_LOCAL_LINK_ADDRESSES_PROPERTY = "org.knowhowlab.osgi.niis.lan_ip6_local_link_addresses";

    public static final String REFRESH_DELAY_DEFAULT = String.valueOf(TimeUnit.SECONDS.toMillis(5));
    public static final String LAN_IP4_PRIVATE_ADDRESSES_DEFAULT = "10.0.0.0/8,172.16.0.0/12,192.168.0.0/16";
    public static final String LAN_IP6_PRIVATE_ADDRESSES_DEFAULT = "fd00::/8";
    public static final String LAN_IP4_LOCAL_LINK_ADDRESSES_DEFAULT = "169.254.0.0/16";
    public static final String LAN_IP6_LOCAL_LINK_ADDRESSES_DEFAULT = "fe80::/10";

    private BundleContext bc;
    private ScheduledExecutorService pool;
    private long refreshDelay;

    @Override
    public void start(BundleContext bc) throws Exception {
        this.bc = bc;

        // read props
        refreshDelay = parseLong(ofNullable(bc.getProperty(REFRESH_DELAY_PROPERTY)).orElse(REFRESH_DELAY_DEFAULT));

        pool = Executors.newScheduledThreadPool(0);
        pool.schedule(new NetworkMonitor(), 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop(BundleContext bc) throws Exception {
        pool.shutdownNow();
    }

    private class NetworkMonitor implements Runnable {
        @Override
        public void run() {
            try {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                Collections.list(networkInterfaces).forEach(networkInterface -> {
                    bc.registerService(NetworkAdapter.class, new NetworkAdapterImpl(networkInterface), null);
                });
            } catch (Exception e) {
                // todo
                e.printStackTrace();
            }
        }

    }

}
