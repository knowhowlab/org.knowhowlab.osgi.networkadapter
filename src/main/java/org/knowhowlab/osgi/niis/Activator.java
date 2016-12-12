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

import org.knowhowlab.osgi.niis.utils.RegistrationManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static org.knowhowlab.osgi.niis.utils.Functions.ofThrowable;

/**
 * @author dpishchukhin
 */
public class Activator implements BundleActivator {
    public static final String REFRESH_DELAY_PROPERTY = "org.knowhowlab.osgi.niis.refresh_delay";

    public static final String REFRESH_DELAY_DEFAULT = String.valueOf(TimeUnit.SECONDS.toMillis(5));

    private BundleContext bc;
    private ScheduledExecutorService pool;
    private long refreshDelay;

    private RegistrationManager manager;

    @Override
    public void start(BundleContext bc) throws Exception {
        this.bc = bc;

        manager = new RegistrationManager(bc::registerService);

        // read props
        refreshDelay = parseLong(ofNullable(bc.getProperty(REFRESH_DELAY_PROPERTY)).orElse(REFRESH_DELAY_DEFAULT));

        pool = Executors.newScheduledThreadPool(0);
        pool.scheduleWithFixedDelay(new NetworkMonitor(), 0, refreshDelay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop(BundleContext bc) throws Exception {
        manager.close();
        pool.shutdownNow();
    }

    private class NetworkMonitor implements Runnable {
        @Override
        public void run() {
            manager.updateServices(ofThrowable(NetworkInterface::getNetworkInterfaces)
                .orElseGet(Collections::emptyEnumeration));
       }
    }
}
