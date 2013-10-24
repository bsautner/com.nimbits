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

import com.nimbits.client.constants.Const;
import com.nimbits.mobile.application.SessionSingleton;

/**
 * Created by benjamin on 10/23/13.
 */
public class InstanceTable extends TableBase {
    public static final String INSTANCES_TABLE_NAME = "SERVER_INSTANCES";
    public static String[] url = {"URL", "TEXT"};
    public static String[] isDefault = {"IS_DEFAULT", "INTEGER"};
    public static String[] apikey = {"API_KEY", "TEXT"};


    public static String getCreateSql() {
        return
                "CREATE TABLE " + INSTANCES_TABLE_NAME + " (" +
                        id[0] + " " + id[1] + "," +
                        url[0] + " " + url[1] + "," +
                        isDefault[0] + " " + isDefault[1] + "," +
                        name[0] + " " + name[1] + "," +
                        apikey[0] + " " + apikey[1] +
                        ");";

    }

    public static String getInitSQL() {
        String defaultKey = null;



        if (SessionSingleton.getInstance().getAppInfo().metaData != null) {
            defaultKey = (String) SessionSingleton.getInstance().getAppInfo().metaData.get(Const.API_KEY_ID);
        }


        return "insert into " + INSTANCES_TABLE_NAME + " (" + url[0] + ", " + isDefault[0] + "," + name[0] + "," + apikey[0] + ") " +
                "VALUES ('cloud.nimbits.com', '1','Public Cloud', '" + defaultKey + "')";
    }

    public static String getInstancesTableName() {
        return INSTANCES_TABLE_NAME;
    }

    public static String  getUrl() {
        return url[0];
    }

    public static String getIsDefault() {
        return isDefault[0];
    }

    public static String getApikey() {
        return apikey[0];
    }
}
