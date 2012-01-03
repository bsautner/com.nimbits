package com.nimbits.client.service.datapoints;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 9/30/11
 * Time: 2:12 PM
 */
public interface PointTransactions {

    List<Point> getPoints() throws NimbitsException;

    Point getPointByID(final long id) throws NimbitsException;

    Point updatePoint(final Point point) throws NimbitsException;

    Point getPointByName(final PointName name) throws NimbitsException;

    void deletePoint(final Point p) throws NimbitsException;

    Point movePoint(final PointName pointName, final CategoryName categoryName) throws NimbitsException;

    Point addPoint(final Point point, final Category c) throws NimbitsException;

    Point addPoint(final PointName pointName, final Category c) throws NimbitsException;

    // Point updatePointStats(final User u, final Point point, final Value v, boolean alarmSent) throws NimbitsException;

    List<Point> getPointsByCategory(final Category c);

    Point checkPoint(final HttpServletRequest req, final EmailAddress email, final Point point);

    Point publishPoint(Point p) throws NimbitsException;

    List<Point> getAllPoints(int start, int end);

    List<Point> getIdlePoints();

    Point getPointByUUID(final String uuid) throws NimbitsException;


}
