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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author dpishchukhin
 */
public class PropertiesCollector<U> {
    private Hashtable<String, Function<U, Object>> functions;

    private PropertiesCollector(Map<String, Function<U, Object>> functions) {
        this.functions = new Hashtable<>(functions);
    }

    public Hashtable<String, Object> collect(U instance) {
        return new Hashtable<>(functions.keySet()
            .stream()
            .collect(Collectors.toMap(Function.identity(), key -> functions.get(key).apply(instance))));
    }

    public static class Builder<T> {
        private Map<String, Function<T, Object>> propertyFunctions;

        public Builder() {
            propertyFunctions = new HashMap<>();
        }

        public Builder<T> addProperty(String key, Function<T, Object> function) {
            propertyFunctions.put(key, function);
            return this;
        }

        public Builder<T> addProperty(String key, Functions.FunctionWithException<T, Object> function, Object defaultValue) {
            propertyFunctions.put(key, property(function, defaultValue));
            return this;
        }

        public Builder<T> addProperty(String key, Object value) {
            propertyFunctions.put(key, t -> value);
            return this;
        }

        public PropertiesCollector<T> build() {
            return new PropertiesCollector<>(propertyFunctions);
        }

        private <R> Function<T, R> property(Functions.FunctionWithException<T, R> function, R defaultValue) {
            return t -> {
                try {
                    return ofNullable(function.apply(t)).orElse(defaultValue);
                } catch (Exception e) {
                    return defaultValue;
                }
            };
        }
    }
}
