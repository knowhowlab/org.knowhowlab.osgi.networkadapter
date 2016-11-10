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

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author dpishchukhin
 */
public class Functions {
    public static <T> Optional<T> ofThrowable(SupplierWithException<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T, R> Optional<R> ofThrowable(FunctionWithException<T, R> function, Supplier<T> supplier) {
        try {
            return Optional.ofNullable(function.apply(supplier.get()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static <T> Optional<T> cast(CastFunction<T> function, Object value) {
        try {
            return Optional.ofNullable(function.apply(value));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    public interface CastFunction<T> {
        T apply(Object value) throws Exception;
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface FunctionWithException<T, R> {
        R apply(T value) throws Exception;
    }

    @FunctionalInterface
    public interface TriFunction<T, U, S, R> {
        R apply(T t, U u, S s);
    }
}
