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

import org.junit.Test;

import java.util.Hashtable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author dpishchukhin
 */
public class PropertiesCollectorTest {
    @Test
    public void collect_simpleValue() throws Exception {
        PropertiesCollector<String> collector = new PropertiesCollector.Builder<String>()
            .addProperty("key", "value")
            .build();

        Hashtable<String, Object> props = collector.collect("test");

        assertThat(props, notNullValue());
        assertThat(props.size(), is(1));
        assertThat(props.keySet().contains("key"), is(true));
        assertThat(props.get("key"), is("value"));
    }

    @Test
    public void collect_simpleFunction() throws Exception {
        PropertiesCollector<String> collector = new PropertiesCollector.Builder<String>()
            .addProperty("key", String::toUpperCase)
            .build();

        Hashtable<String, Object> props = collector.collect("test");

        assertThat(props, notNullValue());
        assertThat(props.size(), is(1));
        assertThat(props.keySet().contains("key"), is(true));
        assertThat(props.get("key"), is("TEST"));
    }

    @Test
    public void collect_functionWithException() throws Exception {
        PropertiesCollector<String> collector = new PropertiesCollector.Builder<String>()
            .addProperty("key", t -> {throw new Exception();}, "default")
            .build();

        Hashtable<String, Object> props = collector.collect("test");

        assertThat(props, notNullValue());
        assertThat(props.size(), is(1));
        assertThat(props.keySet().contains("key"), is(true));
        assertThat(props.get("key"), is("default"));
    }

    @Test
    public void collect_functionWithNullResult() throws Exception {
        PropertiesCollector<String> collector = new PropertiesCollector.Builder<String>()
            .addProperty("key", t -> null, "default")
            .build();

        Hashtable<String, Object> props = collector.collect("test");

        assertThat(props, notNullValue());
        assertThat(props.size(), is(1));
        assertThat(props.keySet().contains("key"), is(true));
        assertThat(props.get("key"), is("default"));
    }

    @Test
    public void collect_complex() throws Exception {
        PropertiesCollector<String> collector = new PropertiesCollector.Builder<String>()
            .addProperty("key1", "value1")
            .addProperty("key2", String::toUpperCase)
            .addProperty("key3", t -> null, "value2")
            .addProperty("key4", t -> {throw new Exception();}, "value3")
            .build();

        Hashtable<String, Object> props = collector.collect("test");

        assertThat(props, notNullValue());
        assertThat(props.size(), is(4));
        assertThat(props.get("key1"), is("value1"));
        assertThat(props.get("key2"), is("TEST"));
        assertThat(props.get("key3"), is("value2"));
        assertThat(props.get("key4"), is("value3"));
    }
}
