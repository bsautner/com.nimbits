/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android.database;

import android.content.Context;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/6/11
 * Time: 11:15 AM
 */
public class DatabaseHelperFactory {
    private static DatabaseHelperImpl instance;
    private static Context context;

    public static DatabaseHelper getInstance(Context aContext)
    {
        if (context != null && ! context.equals(aContext))  {
            instance = null;
        }
        if (instance == null) {
            instance = new DatabaseHelperImpl(aContext);
        }
        return instance;
    }

}
