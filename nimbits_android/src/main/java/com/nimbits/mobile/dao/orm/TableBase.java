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

package com.nimbits.mobile.dao.orm;

/**
 * Created by benjamin on 10/23/13.
 */
public class TableBase {

    public static String[] id = {"_id", "INTEGER PRIMARY KEY autoincrement"};
    public static String[] name = {"NAME", "TEXT"};


    public static String getId() {
        return id[0];
    }

    public static String getName() {
        return name[0];
    }
}
