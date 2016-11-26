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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;

/**
 * @author dpishchukhin
 */
public class FunctionsTest {
    @Test
    public void ofThrowable_noError() throws Exception {
        Assert.assertThat(Functions.ofThrowable(() -> "test"), is(Optional.of("test")));
    }

    @Test
    public void ofThrowable_withError() throws Exception {
        Assert.assertThat(Functions.ofThrowable(() -> {
            throw new Exception("Error");
        }), is(Optional.empty()));

    }
}
