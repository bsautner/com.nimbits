package com.nimbits.client;


import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
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

    Value recordValue(final PointName pointName, final double value, final Date timestamp);

    Value recordValueWithGet(final PointName pointName, final double value, final Date timestamp) throws IOException, NimbitsException;

    Value recordValue(final String pointName, final double value, final Date timestamp);

    String recordBatch(final String params);

    Value recordValue(final PointName pointName, final Value v) throws IOException;

    Value recordValue(final String pointName, final double v);

    Value recordValue(final PointName pointName, double d);

    Category addCategory(final CategoryName categoryName) throws UnsupportedEncodingException;

    String deleteCategory(final CategoryName categoryName);


    Point getPoint(final PointName pointName) throws NimbitsException;

    Point updatePoint(final Point p);

    void deletePoint(final PointName pointName);

    void deletePoint(final String pointName);

    Point addPoint(final Point p, final CategoryName categoryName);

    Point addPoint(final CategoryName categoryName, final PointName pointName);

    Point addPoint(final String pointName);

    @SuppressWarnings({"SameParameterValue", "SameParameterValue"})
    List<Category> getCategories(final boolean includePoints, final boolean includeDiagrams) throws NimbitsException;

    @SuppressWarnings({"SameParameterValue"})
    Category getCategory(final CategoryName categoryName, final boolean includePoints, final boolean includeDiagrams) throws NimbitsException;

    String currentValue(final PointName pointName) throws IOException, NimbitsException;

    Value getCurrentRecordedValue(final PointName pointName);

    List<Value> getSeries(final PointName pointName, final int count) throws NimbitsException;

    List<Value> getSeries(final String pointName, final int count) throws NimbitsException;

    List<Value> getSeries(final PointName pointName, final Date startDate, final Date endDate) throws NimbitsException;

    void downloadSeries(final PointName pointName, final Date startDate, final Date endDate, final String filename) throws IOException, NimbitsException;

    List<Value> loadSeriesFile(final String fileName) throws IOException;

    Object getCurrentDataObject(final PointName pointName, Class<?> cls);

    Value recordDataObject(final PointName pointName, Object object, Class<?> cls) throws NimbitsException;

    Value recordDataObject(PointName pointName, Object object, Class<?> cls, double latitude, double longitude, double value) throws NimbitsException;

    NimbitsUser getNimbitsUser();

    GoogleUser getGoogleUser();

    String getHost();


    byte[] getChartImage(final String baseURL, final String params);
}
