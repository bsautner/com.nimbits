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

package com.nimbits.client;


import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.user.GoogleUser;
import com.nimbits.user.NimbitsUser;
import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: benjamin
 * Date: 3/13/11
 * Time: 12:38 PM
 */
public interface NimbitsClient {

    List<User> getUsers() throws NimbitsException;

    Cookie getAuthCookie();

    boolean isLoggedIn() throws NimbitsException;

    String getChart(final String points, final int count) throws NimbitsException;

    String getChartURL(final String points, final int count, final String additionalParams);

    Value recordValue(final EntityName pointName, final double value, final Date timestamp);

    Value recordValueWithGet(final EntityName pointName, final double value, final Date timestamp) throws IOException, NimbitsException;

    Value recordValue(final String pointName, final double value, final Date timestamp);

    String recordBatch(final String params);

    Value recordValue(final EntityName pointName, final Value v) throws IOException;

    Value recordValue(final String pointName, final double v);

    Value recordValue(final EntityName pointName, double d);

    Entity addCategory(final EntityName categoryName) throws UnsupportedEncodingException;

    String deleteCategory(final EntityName categoryName);

    Point getPoint(final EntityName pointName) throws NimbitsException;

    Point updatePoint(final Point p);

    void deletePoint(final EntityName pointName);

    void deletePoint(final String pointName);

    Point addPoint(final EntityName pointName, final Point p);

    Point addPoint(final EntityName pointName);

    Point addPoint(final String pointName);

    List<Entity> getCategories(final boolean includePoints, final boolean includeDiagrams) throws NimbitsException;

    Entity getCategory(final EntityName categoryName, final boolean includePoints, final boolean includeDiagrams) throws NimbitsException;

    String currentValue(final EntityName pointName) throws IOException, NimbitsException;

    Value getCurrentRecordedValue(final EntityName pointName);

    List<Value> getSeries(final EntityName pointName, final int count) throws NimbitsException;

    List<Value> getSeries(final String pointName, final int count) throws NimbitsException;

    List<Value> getSeries(final EntityName pointName, final Date startDate, final Date endDate) throws NimbitsException;

    void downloadSeries(final EntityName pointName, final Date startDate, final Date endDate, final String filename) throws IOException, NimbitsException;

    List<Value> loadSeriesFile(final String fileName) throws IOException;

    Object getCurrentDataObject(final EntityName pointName, Class<?> cls);

    Value recordDataObject(final EntityName pointName, Object object, Class<?> cls) throws NimbitsException;

    Value recordDataObject(EntityName pointName, Object object, Class<?> cls, double latitude, double longitude, double value) throws NimbitsException;

    NimbitsUser getNimbitsUser();

    GoogleUser getGoogleUser();

    String getHost();


    byte[] getChartImage(final String baseURL, final String params);

    void addCalculation(Calculation calculation, EntityName name);
}
