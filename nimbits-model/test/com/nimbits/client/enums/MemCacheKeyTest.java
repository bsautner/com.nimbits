/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.enums;

import org.junit.Test;

import java.util.zip.CheckedInputStream;

import static org.junit.Assert.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: benjamin
 * Date: 8/8/12
 * Time: 12:00 PM
 */
public class MemCacheKeyTest {

    @Test
    public void checkRepeated() {

        for (MemCacheKey key : MemCacheKey.values()) {

            for (MemCacheKey key2 : MemCacheKey.values()) {
                if (! key.equals(key2)) {
                    assertFalse(key.getText().equals(key2.getText()));
                }
            }

        }

    }
}
