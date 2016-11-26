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

package org.knowhowlab.osgi.niis.model;

import org.osgi.dto.DTO;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

/**
 * @author dpishchukhin
 */
public class RegistrationDTO<T> extends DTO {
    public final T instance;
    public final ServiceRegistration<T> serviceRegistration;
    public final Hashtable<String, Object> properties;

    public RegistrationDTO(T instance, ServiceRegistration<T> serviceRegistration, Hashtable<String, Object> properties) {
        this.instance = instance;
        this.serviceRegistration = serviceRegistration;
        this.properties = properties;
    }
}
