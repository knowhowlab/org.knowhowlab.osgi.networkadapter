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

import org.junit.Before;
import org.junit.Test;
import org.knowhowlab.osgi.networkadapter.impl.AbstractInstance;
import org.osgi.framework.ServiceRegistration;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.osgi.framework.Constants.SERVICE_PID;

/**
 * @author dpishchukhin
 */
public class RegistrationManagerTest {
    private RegistrationManager<SourceInstance, String, StringInstance> manager;

    @Before
    public void setUp() throws Exception {
        manager = createManager();
    }

    @Test
    public void updateServices_registration() throws Exception {
        manager.updateServices(singletonList(new SourceInstance("test instance", "test name")));

        Map<String, RegistrationManager.RegistrationDTO<String, StringInstance>> registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.size(), is(1));
        assertThat(registrations.keySet().contains("test instance"), is(true));

        RegistrationManager.RegistrationDTO<String, StringInstance> dto = registrations.get("test instance");
        assertThat(dto, notNullValue());
        assertThat(dto.instance, notNullValue());
        assertThat(dto.instance.getId(), is("test instance"));
        assertThat(dto.properties.get("id"), is("test instance"));
        assertThat(dto.properties.get("name"), is("test name"));
        assertThat(dto.serviceRegistration, notNullValue());
    }

    @Test
    public void updateServices_unregistration() throws Exception {
        manager.updateServices(singletonList(new SourceInstance("test instance", "test name")));

        Map<String, RegistrationManager.RegistrationDTO<String, StringInstance>> registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.size(), is(1));

        manager.updateServices(Collections.emptyList());
        registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.isEmpty(), is(true));
    }

    @Test
    public void updateServices_update() throws Exception {
        manager.updateServices(singletonList(new SourceInstance("test instance", "test name")));

        Map<String, RegistrationManager.RegistrationDTO<String, StringInstance>> registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.size(), is(1));

        manager.updateServices(singletonList(new SourceInstance("test instance", "test name 2")));

        registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.size(), is(1));
        assertThat(registrations.keySet().contains("test instance"), is(true));

        RegistrationManager.RegistrationDTO<String, StringInstance> dto = registrations.get("test instance");
        assertThat(dto, notNullValue());
        assertThat(dto.instance, notNullValue());
        assertThat(dto.instance.getId(), is("test instance"));
        assertThat(dto.properties.get("id"), is("test instance"));
        assertThat(dto.properties.get("name"), is("test name 2"));
        assertThat(dto.serviceRegistration, notNullValue());
    }

    @Test
    public void close() throws Exception {
        manager.updateServices(singletonList(new SourceInstance("test instance", "test name")));

        Map<String, RegistrationManager.RegistrationDTO<String, StringInstance>> registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.size(), is(1));

        manager.close();

        registrations = manager.getRegistrations();
        assertThat(registrations, notNullValue());
        assertThat(registrations.isEmpty(), is(true));
    }

    private RegistrationManager<SourceInstance, String, StringInstance> createManager() {
        return new RegistrationManager.Builder<SourceInstance, String, StringInstance>(String.class)
            .withNewInstanceFunction(StringInstance::new)
            .withRegistrationFunction(createMockRegistrationFunction())
            .withIdFunction(SourceInstance::getId)
            .withPropertiesCollector(new PropertiesCollector.Builder<SourceInstance>()
                .addProperty("id", SourceInstance::getId)
                .addProperty("name", SourceInstance::getName)
                .addProperty(SERVICE_PID, SourceInstance::getId)
                .build()
            )
            .build();
    }

    private Functions.TriFunction<Class<String>, StringInstance, Dictionary, ServiceRegistration<String>> createMockRegistrationFunction() {
        return (c, i, d) -> {
            ServiceRegistration registration = mock(ServiceRegistration.class);
            doNothing().when(registration).unregister();
            //noinspection unchecked
            doNothing().when(registration).setProperties(any(Dictionary.class));
            return registration;
        };
    }

    private static class SourceInstance {
        private String id;
        private String name;

        SourceInstance(String id, String name) {
            this.id = id;
            this.name = name;
        }

        String getId() {
            return id;
        }

        String getName() {
            return name;
        }
    }

    private static class StringInstance extends AbstractInstance<SourceInstance> {
        StringInstance(SourceInstance source, Map<String, Object> properties) {
            super(source, properties);
        }

        @Override
        public String getId() {
            return source.getId();
        }
    }
}
