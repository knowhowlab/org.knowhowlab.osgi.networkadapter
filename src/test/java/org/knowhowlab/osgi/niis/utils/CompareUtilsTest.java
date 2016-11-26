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

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.knowhowlab.osgi.niis.utils.CompareUtils.deepEquals;

/**
 * @author dpishchukhin
 */
@SuppressWarnings("ALL")
public class CompareUtilsTest {
    @Test
    public void deepEqual_null() throws Exception {
        Assert.assertThat(deepEquals(null, null), is(true));
    }

    @Test
    public void deepEqual_same() throws Exception {
        Map m1 = new HashMap();
        Map m2 = m1;
        Assert.assertThat(deepEquals(m1, m2), is(true));
    }

    @Test
    public void deepEqual_size() throws Exception {
        Map m1 = new HashMap();
        m1.put("key", "value");
        Map m2 = new HashMap();
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_differentKeys() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", "value");
        Map m2 = new HashMap();
        m2.put("key2", "value");
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_differentValues() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", "value1");
        Map m2 = new HashMap();
        m2.put("key1", "value2");
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_byllValue() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", null);
        Map m2 = new HashMap();
        m2.put("key1", "value2");
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_differentValueClasses() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", "value1");
        Map m2 = new HashMap();
        m2.put("key1", 1);
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_ObjectArray_Integers() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", new Integer[] {1, 2, 3});
        Map m2 = new HashMap();
        m2.put("key1", new Integer[] {1, 2, 3});
        Assert.assertThat(deepEquals(m1, m2), is(true));
    }

    @Test
    public void deepEqual_ObjectArray_Strings() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", new String[] {"a", "b", "c"});
        Map m2 = new HashMap();
        m2.put("key1", new String[] {"a", "b", "c"});
        Assert.assertThat(deepEquals(m1, m2), is(true));
    }

    @Test
    public void deepEqual_ObjectArray_Strings_notEqual() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", new String[] {"a", "b", "c"});
        Map m2 = new HashMap();
        m2.put("key1", new String[] {"a", "b", "d"});
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_StringsArray_Integer() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", new String[] {"a", "b", "c"});
        Map m2 = new HashMap();
        m2.put("key1", 1);
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }

    @Test
    public void deepEqual_ByteArray() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", new byte[] {1, 2, 3});
        Map m2 = new HashMap();
        m2.put("key1", new byte[] {1, 2, 3});
        Assert.assertThat(deepEquals(m1, m2), is(true));
    }

    @Test
    public void deepEqual_ByteArray_notEqual() throws Exception {
        Map m1 = new HashMap();
        m1.put("key1", new byte[] {1, 2, 3});
        Map m2 = new HashMap();
        m2.put("key1", new byte[] {1, 2, 4});
        Assert.assertThat(deepEquals(m1, m2), is(false));
    }
}
