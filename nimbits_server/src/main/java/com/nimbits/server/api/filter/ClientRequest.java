/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.api.filter;

import java.io.Serializable;

public class ClientRequest implements Serializable {

    private final long timestamp;
    private final int counter;

    public ClientRequest(long timestamp, int counter) {

        this.timestamp = timestamp;
        this.counter = counter;
    }

    public boolean isOk() {
        return true;// timestamp + 5000 < System.currentTimeMillis();
    }

    public int getCounter() {
        return counter;
    }
}
