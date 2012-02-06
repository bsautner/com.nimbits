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


import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.diagram.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.category.*;
import com.nimbits.server.common.*;
import com.nimbits.server.core.*;
import com.nimbits.server.diagram.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.point.*;
import com.nimbits.server.recordedvalue.*;
import com.nimbits.server.user.*;
import com.nimbits.shared.*;

import java.util.*;
import java.util.logging.*;

public class CategoryServiceImpl extends RemoteServiceServlet implements CategoryService

{
    private static final Logger log = Logger.getLogger(CategoryServiceImpl.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private String getServerName() {
        return ServerInfoImpl.getFullServerURL(this.getThreadLocalRequest());
    }

    private void sendToCore(Category c) {
        if (! c.getName().getValue().equals(Const.CONST_HIDDEN_CATEGORY)) {
            String j = GsonFactory.getInstance().toJson(c);
            CoreFactory.getInstance().reportUpdateToCore(getServerName(), j, EntityType.category);
        }
    }

    // called from RPC client
    @Override
    public List<Category> getCategories(final boolean includePoints,
                                        final boolean includeDiagrams,
                                        final boolean includeSubscriptions,
                                        final boolean includeAlertState) throws NimbitsException {

        final List<Category> categoryList;
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());


        if (u != null) {
            categoryList = getCategories(u, includePoints, includeDiagrams, includeSubscriptions);

            for (final Category c : categoryList) {
                if (c.getPoints() != null) {
                    for (final Point p : c.getPoints()) {
                        if (includeAlertState) {
                            final Value v = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
                            if (v != null) {
                                p.setAlertState(v.getAlertState());
                                p.setValue(v);
                            }
                        }
                        p.setReadOnly(u.getId() != p.getUserFK());
                    }
                }
            }
        } else {
            throw new NimbitsException("There is a problem with your Nimbits account and we could not " +
                    "retrieve your points. Please report this error to support@nimbits.com");
        }
        return categoryList;

    }


    public List<Category> getCategories(final User u,
                                        final boolean includePoints,
                                        final boolean includeDiagrams,
                                        final boolean includeSubscriptions) {


        return CategoryTransactionFactory.getInstance(u).getCategories(includePoints, includeDiagrams, includeSubscriptions);
    }

    public Category addCategory(final CategoryName categoryName) throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        Category c = addCategory(u, categoryName);
        sendToCore(c);
        return c;

    }

    public Category addCategory(final User u, final CategoryName categoryName) {


        Category c = CategoryTransactionFactory.getInstance(u).addCategory(categoryName);
        sendToCore(c);
        return c;

    }

    @Override
    public Category getCategoryByName(final CategoryName categoryName,
                                      final boolean includePoints,
                                      final boolean includeDiagrams) throws NimbitsException {



        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());


        return getCategoryByName(u, categoryName, includePoints, includeDiagrams);

    }
    @Override
    public Category getCategoryByName(final User u,
                                      final CategoryName categoryName,
                                      final boolean includePoints,
                                      final boolean includeDiagrams) throws NimbitsException {

        final Category c = CategoryServiceFactory.getInstance().getCategory(u, categoryName);
        if (includePoints) {
            final List<Point> points = PointServiceFactory.getInstance().getPointsByCategory(u, c);
            c.setPoints(points);
        }

        if (includeDiagrams) {
            final List<Diagram> diagrams = DiagramServiceFactory.getInstance().getDiagramsByCategory(u,c);
            c.setDiagrams(diagrams);
        }
        return c;

    }
    public void deleteCategory(final User u, final Category c) throws NimbitsException {

        CategoryTransactionFactory.getInstance(u).deleteCategory(c);

        CoreFactory.getInstance().reportDeleteToCore(GsonFactory.getInstance().toJson(c), EntityType.category);
    }

    public void deleteCategory(final Category c) throws NimbitsException {
        // delete me UserContext context = UserContextFactory.createUserContext(CategoryServiceImpl.class.getName());
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        deleteCategory(u, c);

    }

    @Override
    public List<Category> getConnectionCategories(final boolean includePoints,
                                                  final boolean includeDiagrams,
                                                  final boolean includeSubscriptions,
                                                  final EmailAddress email) throws NimbitsException {

        final User connectionUser = UserTransactionFactory.getInstance().getNimbitsUser(email);
        final User loggedInUser = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        if (connectionUser != null && loggedInUser != null) {

            final List<Category> result = getCategories(connectionUser, includePoints, includeDiagrams,includeSubscriptions);
            for (final Category c : result) {
                for (final Point p : c.getPoints()) {
                    final List<Point> approvedPoints = new ArrayList<Point>();
                    final List<Diagram> approvedDiagrams = new ArrayList<Diagram>();

                    if (PointServiceFactory.getInstance().checkPointProtection(loggedInUser, connectionUser, p)) {
                        approvedPoints.add(p);
                    }
                    c.setPoints(approvedPoints);


                    for (final Diagram d : c.getDiagrams()) {
                        if (DiagramServiceFactory.getInstance().checkDiagramProtection(loggedInUser, connectionUser, d)) {
                            approvedDiagrams.add(d);
                        }
                        c.setDiagrams(approvedDiagrams);

                    }
                }
            }

            return result;
        } else {
            return null;
        }
    }

    @Override
    public Category getCategory(final User user, final CategoryName categoryName) {
        return CategoryTransactionFactory.getInstance(user).getCategory(categoryName);
    }

    @Override
    public Category createHiddenCategory(User u) {
        return CategoryTransactionFactory.getInstance(u).createHiddenCategory();
    }

    @Override
    public boolean categoryExists(User u, CategoryName categoryName) throws NimbitsException {
        return CategoryTransactionFactory.getInstance(u).categoryExists(categoryName);
    }

    @Override
    public Category getCategory(User u, long catID) {
        return CategoryTransactionFactory.getInstance(u).getCategory(catID);
    }

    @Override
    public Category updateCategory(Category category) throws NimbitsException {

        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());

        Category c = CategoryTransactionFactory.getInstance(u).updateCategory(category);
        sendToCore(c);
        return c;
    }

    @Override
    public Category updateCategory(User u, Category category) {
        Category c = CategoryTransactionFactory.getInstance(u).updateCategory(category);
        sendToCore(c);
        return c;
    }

    @Override
    public Category publishCategory(Category category) throws NimbitsException {
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(
                this.getThreadLocalRequest());
        category.setProtectionLevel(ProtectionLevel.everyone);
        if (Utils.isEmptyString(category.getUUID())) {
            category.setUUID(UUID.randomUUID().toString());
        }
        List<Point> points = PointServiceFactory.getInstance().getPointsByCategory(u, category);
        for (Point point : points) {
            PointServiceFactory.getInstance().publishPoint(u, point);
        }
        updateCategory(category);
        // String j = GsonFactory.getInstance().toJson(category);
        // CoreFactory.getInstance().reportUpdateToCore(getServerName(),j,EntityType.category);
        return updateCategory(category);

    }

    @Override
    public Category getCategoryByUUID(String uuidParam) {
        return CategoryTransactionFactory.getInstance(null).getCategoryByUUID(uuidParam);
    }


}
