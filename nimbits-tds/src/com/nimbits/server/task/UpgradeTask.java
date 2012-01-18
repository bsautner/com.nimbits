package com.nimbits.server.task;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Calculation;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.user.User;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.point.PointTransactionsFactory;
import com.nimbits.server.user.UserTransactionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/14/12
 * Time: 10:48 AM
 */
public class UpgradeTask  extends HttpServlet

{

    private static final Logger log = Logger.getLogger(UpgradeTask.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
        upgradeCategories();
        fixCalcs();
    }
    private void upgradeCategories() {
        int set = 0;
        int results = -1;

        while (results != 0) {
            final List<User> users = UserTransactionFactory.getInstance().getUsers(set, set + Const.CONST_QUERY_CHUNK_SIZE);
            results = users.size();

            set += Const.CONST_QUERY_CHUNK_SIZE;
            for (User u : users) {
                TaskFactoryLocator.getInstance().startCategoryMaintTask(u);

            }

        }
    }

    private void fixCalcs() {
        int set = 0;
        int results = -1;

        while (results != 0) {
            List<Point> points = PointServiceFactory.getInstance().getAllPoints(set, set + Const.CONST_QUERY_CHUNK_SIZE);
            results = points.size();
            set += Const.CONST_QUERY_CHUNK_SIZE;
            for (Point p : points) {
                try {

                    fixCalc(p);
                } catch (NimbitsException e) {
                  log.severe(e.getMessage());
                }
            }
        }
    }

    private void fixCalc(Point p ) throws NimbitsException {
        if (p.getCalculation() == null) {

            if (p.getFormula() != null) {

                String formula = p.getFormula();
                long x = 0;
                long y = 0;
                long z = 0;
                long target = 0;
                boolean enabled = true;

                if (p.getX() > 0) {
                    Point px = PointTransactionsFactory.getDaoInstance(null).getPointByID(p.getX());
                    if (px != null) {
                        x = p.getX();
                    } else {
                        enabled = false;
                    }
                }

                if (p.getY() > 0) {
                    Point py = PointTransactionsFactory.getDaoInstance(null).getPointByID(p.getY());
                    if (py != null) {
                        y = p.getY();
                    } else {
                        enabled = false;
                    }
                }
                if (p.getZ() > 0) {
                    Point pz = PointTransactionsFactory.getDaoInstance(null).getPointByID(p.getZ());
                    if (pz != null) {
                        z = p.getZ();
                    } else {
                        enabled = false;
                    }
                }
                if (p.getTarget() > 0) {
                    Point pt = PointTransactionsFactory.getDaoInstance(null).getPointByID(p.getTarget());
                    if (pt != null) {
                        target = p.getTarget();
                    } else {
                        enabled = false;
                    }
                }

                Calculation calculation = PointModelFactory.createCalculation(enabled, formula, target, x, y, z);
                p.setCalculation(calculation);
                User u = UserTransactionFactory.getDAOInstance().getNimbitsUserByID(p.getUserFK());

                Point r = PointTransactionsFactory.getInstance(u).updatePoint(p);
                if (r.getCalculation() == null) {
                    log.severe("Failed to upgrade a cals" + p.getName());
                }
            }
        }
    }
}
