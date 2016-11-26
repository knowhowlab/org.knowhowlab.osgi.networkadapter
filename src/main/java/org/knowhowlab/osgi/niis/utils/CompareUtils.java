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

package org.knowhowlab.osgi.niis.utils;

import java.util.Arrays;
import java.util.Map;

/**
 * @author dpishchukhin
 */
public class CompareUtils {
    private CompareUtils() {
    }

    public static boolean deepEquals(Map<String, Object> m1, Map<String, Object> m2) {
        if (m2 == m1)
            return true;

        if (m2.size() != m1.size())
            return false;

        try {
            for (Map.Entry<String, Object> e : m1.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (!(m2.get(key) == null && m2.containsKey(key)))
                        return false;
                } else if (value instanceof byte[]) {
                    if (!Arrays.equals((byte[]) value, (byte[]) m2.get(key)))
                        return false;
                } else if (value.getClass().isArray()) {
                    if (!Arrays.equals((Object[]) value, (Object[]) m2.get(key)))
                        return false;
                } else {
                    if (!value.equals(m2.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
        return true;
    }
}
