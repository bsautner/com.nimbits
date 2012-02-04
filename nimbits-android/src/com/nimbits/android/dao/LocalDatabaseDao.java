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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.android.dao;

import android.content.ContentValues;
import android.content.Context;
import android.widget.ListAdapter;
import com.nimbits.client.model.point.PointName;

import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/6/11
 * Time: 5:00 PM
 */
public interface LocalDatabaseDao {

    String getSetting(final Context aContext, final String settingName);

    void insertPoints(final Context aContext, final ContentValues values);

    void insertMain(final Context aContext, final ContentValues values);

    ListAdapter mainListCursor(final Context aContext);

    void updatePointValuesByName(final Context aContext, final ContentValues u, final PointName pointName);

    void updateSetting(final Context aContext, final String settingName, final String newValue);

    void addServer(final Context aContext, final String url);

    String getSelectedChildTableJsonByName(final Context aContext, final String name);

    void deleteAll(final Context aContext);

    List<String> getServers(final Context aContext);
}
