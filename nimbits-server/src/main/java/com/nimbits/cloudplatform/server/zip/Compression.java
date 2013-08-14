/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.cloudplatform.server.zip;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/27/12
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Compression {
    byte[] decompress(byte[] input) throws UnsupportedEncodingException, IOException, DataFormatException;
}
