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

package com.nimbits.server.pointcategory;

import com.google.gson.Gson;
import com.google.gwt.core.client.GWT;
import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.user.User;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.user.UserServiceFactory;
import com.nimbits.shared.Utils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CategoryWebService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final static Gson gson = GsonFactory.getInstance();

    private Action getAction(final String param) {

        if (Utils.isEmptyString(param)) {
            return Action.create;
        } else if (param.equals(Const.ACTION_CREATE)) {
            return Action.create;
        } else if (param.equals(Const.ACTION_DELETE)) {
            return Action.delete;
        } else if (param.equals(Const.ACTION_UPDATE)) {
            return Action.update;
        } else {
            return Action.create;
        }
    }

    @Override
    public void doPost(final HttpServletRequest req,
                       final HttpServletResponse resp) {
        try {
            final PrintWriter out = resp.getWriter();
            final String categoryNameParam = req.getParameter(Const.PARAM_NAME);
            final String actionParam = req.getParameter(Const.PARAM_ACTION);
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            final Action action = getAction(actionParam);
            // delete me UserContext context = UserContextFactory.createUserContext(CategoryWebService.class.getName());

            if (u != null && !u.isRestricted() && !Utils.isEmptyString(categoryNameParam)) {

                final EntityName name = CommonFactoryLocator.getInstance().createName(categoryNameParam);
                switch (action) {
                    case create:
                        if (CategoryServiceFactory.getInstance().categoryExists(u, name)) {
                            out.println(Const.RESPONSE_CATEGORY_EXISTS);
                        } else {
                            final Category c = CategoryServiceFactory.getInstance().addCategory(u, name);
                            out.print(gson.toJson(c));
                        }
                        return;
                    case delete: {
                        final Category c = CategoryServiceFactory.getInstance().getCategory(u, name);
                        if (c != null) {
                            CategoryServiceFactory.getInstance().deleteCategory(u, c);
                            out.println(gson.toJson(c));
                        }
                    }
                }
            }
        } catch (IOException e) {
            GWT.log(e.getMessage(), e);
        } catch (NimbitsException e) {
            GWT.log(e.getMessage(), e);
        }

    }

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp) {

        String result;


        final String includePointsStr = req.getParameter(Const.PARAM_INCLUDE_POINTS);
        final String includeDiagramsStr = req.getParameter(Const.PARAM_INCLUDE_DIAGRAMS);
        final String includeSubscriptionsStr = req.getParameter(Const.PARAM_INCLUDE_SUBSCRIPTIONS);
        final String categoryNameParam = req.getParameter(Const.PARAM_NAME);

        final boolean includePoints = includePointsStr != null && includePointsStr.equalsIgnoreCase(Const.WORD_TRUE);
        final boolean includeDiagrams = includeDiagramsStr != null && includeDiagramsStr.equalsIgnoreCase(Const.WORD_TRUE);
        final boolean includeSubscriptions = includeSubscriptionsStr != null && includeSubscriptionsStr.equalsIgnoreCase(Const.WORD_TRUE);

        try {
            final PrintWriter out = resp.getWriter();
            final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);
            if (categoryNameParam == null) {

                final List<Category> categories = CategoryServiceFactory.getInstance().getCategories(u,
                        includePoints, includeDiagrams,includeSubscriptions);

//                for (final Category category : Categories) {
//                    if (includePoints && category.getPoints() != null) {
//                        final String j = gson.toJson(category.getPoints(), GsonFactory.pointListType);
//                        category.setPoints(null);
//                        category.setJsonPointCollection(j);
//                    }
//
//                    if (includeDiagrams && category.getDiagrams() != null) {
//                        final String j = gson.toJson(category.getDiagrams(), GsonFactory.diagramListType);
//                        category.setDiagrams(null);
//                        category.setJsonDiagramCollection(j);
//
//                    }
//
//
//                }

                result = gson.toJson(categories, GsonFactory.categoryListType);
                out.println(result);
            } else {

                final EntityName categoryName = CommonFactoryLocator.getInstance().createName(categoryNameParam);

                final Category c = CategoryServiceFactory.getInstance().getCategory(u, categoryName);

//                if (includePoints) {
//                    final List<Point> points = PointTransactionsFactory.getInstance().getPointsByCategory(c, u);
//                    final String j = gson.toJson(points, GsonFactory.pointListType);
//                    c.setJsonPointCollection(j);
//                }
                result = gson.toJson(c);
                out.println(result);
            }


        } catch (IOException e) {
            GWT.log(e.getMessage(), e);
        } catch (NimbitsException e) {
            GWT.log(e.getMessage(), e);
        }

    }

}
