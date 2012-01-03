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

package com.nimbits.client;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.nimbits.client.controls.MainMenuToolBar;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.exceptions.DiagramNotFoundException;
import com.nimbits.client.exceptions.NotLoggedInException;
import com.nimbits.client.exceptions.ObjectProtectionException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.LoginInfo;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.diagram.Diagram;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.panels.*;
import com.nimbits.client.service.LoginService;
import com.nimbits.client.service.LoginServiceAsync;
import com.nimbits.client.service.category.CategoryService;
import com.nimbits.client.service.category.CategoryServiceAsync;
import com.nimbits.client.service.diagram.DiagramService;
import com.nimbits.client.service.diagram.DiagramServiceAsync;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;
import com.nimbits.client.service.settings.SettingsService;
import com.nimbits.client.service.settings.SettingsServiceAsync;
import com.nimbits.client.service.twitter.TwitterService;
import com.nimbits.client.service.twitter.TwitterServiceAsync;
import com.nimbits.shared.Utils;

import java.util.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class nimbits implements EntryPoint {

    private MainPanel mainPanel;
    private LoginInfo loginInfo = null;
    private Viewport viewport;
    private final static String heading = (Const.CONST_SERVER_NAME + " " + Const.CONST_SERVER_VERSION);


    private void loadLayout(final LoginInfo loginInfo, final boolean doAndroid,
                            final Map<String, String> settings) throws NimbitsException {

        final ContentPanel contentPanel = new ContentPanel(new FillLayout());


        final String logoutUrl = (loginInfo != null) ? loginInfo.getLogoutUrl() : Const.PATH_NIMBITS_HOME;


        final boolean loadConnections = (settings != null && settings.containsKey(Const.SETTING_ENABLE_CONNECTIONS)
                && settings.get(Const.SETTING_ENABLE_CONNECTIONS).equals("1"));


        viewport = new Viewport();
        if (doAndroid) {
            viewport.setLayout(new FillLayout());
            viewport.setBorders(false);
            mainPanel = new MainPanel(loginInfo, doAndroid, false);
            contentPanel.add(mainPanel);
            contentPanel.setHeaderVisible(false);
            contentPanel.setLayout(new FillLayout());
            viewport.add(contentPanel);
        } else {
            viewport.setLayout(new BorderLayout());
            viewport.setBorders(false);

            mainPanel = new MainPanel(loginInfo, false, loadConnections);

            contentPanel.setHeaderVisible(true);
            if (loginInfo != null) {
                contentPanel.setHeading(heading + " " + loginInfo.getEmailAddress().getValue());
            }
            contentPanel.setTopComponent(new MainMenuToolBar(logoutUrl, loginInfo, settings));
            contentPanel.add(mainPanel);
            contentPanel.setLayout(new FillLayout());
            addListeners();
            viewport.add(contentPanel, new BorderLayoutData(LayoutRegion.CENTER));
        }


        viewport.setHeight("100%");
        RootPanel.get("main").add(viewport);

    }

    private void loadDiagramView(final Diagram diagram,
                                 final ClientType clientType) {

        viewport = new Viewport();
        viewport.setLayout(new BorderLayout());
        viewport.setBorders(false);

        final ContentPanel contentPanel = new ContentPanel(new FillLayout());
        contentPanel.setHeaderVisible(true);
        contentPanel.setHeading(Const.HTML_HOME_LINK + " | " + heading + " "
                + diagram.getName());

        diagram.setFullScreenView(true);

        final DiagramPanel diagramPanel = new DiagramPanel(diagram, false, Window.getClientWidth(), Window.getClientHeight());
        diagramPanel.addPointClickedListeners(new NavigationEventProvider.PointClickedListener() {

            @Override
            public void onPointClicked(final Point p) {

                if (clientType == ClientType.other) {
                    showAnnotatedTimeLine(p);
                } else {
                    Window.Location.replace("?" + Const.PARAM_CLIENT + "=" + Const.WORD_ANDROID + "&" + Const.PARAM_POINT + "=" + p.getName());
                }

            }

        });

        diagramPanel.addDiagramClickedListeners(new NavigationEventProvider.DiagramClickedListener() {

            @Override

            public void onDiagramClicked(final Diagram d) {

                RootPanel.get().remove(viewport);
                loadDiagramView(d, clientType);


            }

        });

        diagramPanel.addUrlClickedListeners(new NavigationEventProvider.UrlClickedListener() {

            @Override
            public void onUrlClicked(String url, String target) {
                Window.Location.replace(url);
            }
        });
        diagramPanel.setHeight("100%");
        contentPanel.add(diagramPanel);
        contentPanel.setLayout(new FillLayout());
        viewport.add(contentPanel, new BorderLayoutData(LayoutRegion.CENTER));
        RootPanel.get().add(viewport);

    }

    void showAnnotatedTimeLine(final Point point) {

        final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        final ContentPanel p = new ContentPanel();
        p.setHeading(point.getName().getValue());

        final List<Point> points = Arrays.asList(point);
        //the chart panel will determine the end date for the first show
        final AnnotatedTimeLinePanel annotatedTimeLinePanel = new AnnotatedTimeLinePanel(false, Const.DEFAULT_CHART_NAME);
        //  final Date start = new Date(result.getTime() - (1000 * 60 * 60 * 24) );
        // annotatedTimeLinePanel.setTimespan(new TimespanModel(start, result));
        //  annotatedTimeLinePanel.setPoints(points);

        p.add(annotatedTimeLinePanel);
        p.setWidth(600);
        p.setHeight(400);
        annotatedTimeLinePanel.initChart();
        annotatedTimeLinePanel.addPoint(point);
        w.add(p);
        w.setHeight(400);
        w.setWidth(600);
        w.show();

//        dataService.getLastRecordedDate(points, new AsyncCallback<Date>() {
//            @Override
//            public void onFailure(Throwable caught) {
//
//            }
//
//            @Override
//            public void onSuccess(final Date result) {
//
//
//            }
//
//
//        });
    }

    private void addListeners() {

        mainPanel.addCategoryClickedListeners(new NavigationEventProvider.CategoryClickedListener() {
            //need to getInstance a fresh copy here
            @Override
            public void onCategoryClicked(final Category c, final boolean readOnly) throws NimbitsException {

                final CategoryServiceAsync categoryService = GWT.create(CategoryService.class);
                categoryService.getCategoryByName(c.getName(), true, true, new AsyncCallback<Category>() {


                    @Override
                    public void onFailure(final Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(final Category category) {

                        if (category.getPoints() != null) {
                            for (final Point p : category.getPoints()) {
                                p.setReadOnly(readOnly);
                                try {
                                    mainPanel.addPoint(p);
                                } catch (NimbitsException e) {
                                    GWT.log(e.getMessage());
                                }
                            }
                        }
                    }
                });
            }
        });

        mainPanel.addPointClickedListeners(new NavigationEventProvider.PointClickedListener() {

            @Override
            public void onPointClicked(final Point p) throws NimbitsException {
                mainPanel.addPoint(p);
            }

        });

        mainPanel.addDiagramClickedListeners(new NavigationEventProvider.DiagramClickedListener() {

            @Override
            public void onDiagramClicked(final Diagram d) {
                mainPanel.addDiagram(d);

                // showDiagram(d);
            }

        });
    }


    @Override
    public void onModuleLoad() {

        final String uuid = Location.getParameter(Const.PARAM_UUID);
        final String fb = Location.getParameter(Const.PARAM_FACEBOOK);
        final String code = Location.getParameter(Const.PARAM_CODE);
        final String tw = Location.getParameter(Const.PARAM_TWITTER);
        final String oauth_token = Location.getParameter(Const.PARAM_OAUTH);
        final String diagramUUID = Location.getParameter(Const.PARAM_DIAGRAM);
        final String clientTypeParam = Location.getParameter(Const.PARAM_CLIENT);
        final String debug = Location.getParameter(Const.PARAM_DEBUG);
        boolean doAndroid = false;

        final boolean doFacebook = ((fb != null) || (code != null));
        final boolean doTwitter = ((tw != null) && (oauth_token == null));
        final boolean doTwitterFinish = ((tw != null) && (oauth_token != null));
        final boolean doDiagram = (diagramUUID != null);

        final ClientType clientType;

        final boolean doDebug = (debug != null);


        if (!Utils.isEmptyString(clientTypeParam) && clientTypeParam.equals(Const.WORD_ANDROID)) {
            clientType = ClientType.android;
            doAndroid = true;
        } else {
            clientType = ClientType.other;

        }
        //   doAndroid = true;

        if (doDiagram) {
            processDiagramRequest(diagramUUID, clientType);
        } else {
            try {
                loadPortalView(uuid, code, oauth_token, doFacebook, doTwitter, doTwitterFinish, doAndroid);
            } catch (NimbitsException e) {
                Window.alert(e.getMessage());
            }
        }


    }


    private void processDiagramRequest(final String diagramName, final ClientType clientType) {
        DiagramServiceAsync diagramService = GWT.create(DiagramService.class);
        try {
            diagramService.getDiagramByUuid(diagramName, new AsyncCallback<Diagram>() {
                @Override
                public void onFailure(Throwable throwable) {
                    handleError(throwable);
                }

                @Override
                public void onSuccess(final Diagram diagram) {
                    loadDiagramView(diagram, clientType);
                }
            });
        } catch (ObjectProtectionException e) {
            handleError(e);
        } catch (DiagramNotFoundException e) {
            handleError(e);
        } catch (NimbitsException e) {
            handleError(e);
        }
    }


    private void loadPortalView(final String uuid, final String code, final String oauth_token, final boolean doFacebook,
                                final boolean doTwitter, final boolean doTwitterFinish,
                                final boolean doAndroid) throws NimbitsException {
        SettingsServiceAsync settingService = GWT.create(SettingsService.class);
        settingService.getSettings(new AsyncCallback<Map<String, String>>() {

            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage(), caught);
                Window.Location.replace(loginInfo.getLogoutUrl());
            }

            @Override
            public void onSuccess(final Map<String, String> settings) {

                if (uuid != null) {
                    try {
                        loadSinglePointDisplay(uuid);
                    } catch (NimbitsException e) {
                        Window.alert(e.getMessage());
                    }
                } else if (doFacebook) {
                    finishFacebookAuthentication(settings, code);
                } else if (doTwitterFinish) {
                    try {
                        finishTwitterAuthentication(settings, oauth_token, doTwitter);
                    } catch (NimbitsException e) {
                        Window.alert(e.getMessage());
                    }
                } else {

                    try {
                        loadPortal(doTwitter, doAndroid, settings);
                    } catch (NimbitsException e) {
                        Window.alert(e.getMessage());
                    }

                }

            }

        });
    }

    private void finishFacebookAuthentication(final Map<String, String> settings, final String code) {
        getViewport();
        FacebookPanel fbPanel = new FacebookPanel(code, settings);
        fbPanel.setHeight(500);
        fbPanel.setWidth(600);
        viewport.add(fbPanel);
        viewport.setWidth(600);
        viewport.setHeight(500);
        RootPanel.get("main").add(viewport);
    }

    private void finishTwitterAuthentication(final Map<String, String> settings, final String oauth_token, final boolean doTwitter) throws NimbitsException {
        TwitterServiceAsync twitterService = GWT.create(TwitterService.class);


        twitterService.updateUserToken(oauth_token,
                new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        handleError(caught);

                    }

                    @Override
                    public void onSuccess(Void result) {
                        Window.alert(Const.MESSAGE_TWITTER_ADDED);
                        try {
                            loadPortal(doTwitter, false, settings);
                        } catch (NimbitsException e) {
                            Window.alert(e.getMessage());
                        }
                    }

                });

    }

    private void getViewport() {
        viewport = new Viewport();
        viewport.setLayout(new FillLayout());
        viewport.setBorders(false);
    }

    private void loadSinglePointDisplay(final String uuid) throws NimbitsException {
        Location.replace("report.html?uuid=" + uuid);
    }

    private void loadPortal(final boolean doTwitter, final boolean doAndroid, final Map<String, String> settings) throws NimbitsException {
        LoginServiceAsync loginService = GWT
                .create(LoginService.class);
        loginService.login(GWT.getHostPageBaseURL(),
                new AsyncCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Throwable error) {
                        GWT.log(error.getMessage(), error);
                        handleError(error);
                    }

                    @Override
                    public void onSuccess(LoginInfo result) {


                        loginInfo = result;

                        if (loginInfo.isLoggedIn()) {

                            try {
                                loadLayout(loginInfo, doAndroid, settings);
                            } catch (NimbitsException ignored) {

                            }
                            if (doTwitter) {
                                final TwitterServiceAsync twitterService = GWT.create(TwitterService.class);

                                try {
                                    twitterService.twitterAuthorise(loginInfo.getEmailAddress(), new AsyncCallback<String>() {

                                        @Override
                                        public void onFailure(Throwable caught) {
                                            GWT.log(caught.getMessage(), caught);
                                        }

                                        @Override
                                        public void onSuccess(String result) {

                                            Location.replace(result);
                                        }

                                    });
                                } catch (NimbitsException e) {
                                    Window.alert("There was a problem loading the settings from the server.");
                                }


                            }

                        } else {
                            loadLogin();
                        }
                    }

                });
    }

    private void loadData(final Point point) throws NimbitsException {
        final List<Point> points = new ArrayList<Point>();
        final ContentPanel mainContentPanel = new ContentPanel();
        points.add(point);
        getViewport();

        mainContentPanel.setHeading(point.getName() + "  " + point.getDescription());
        viewport.add(mainContentPanel);
        RootPanel.get("main").add(viewport);
        LoginServiceAsync loginService = GWT.create(LoginService.class);

        loginService.login(GWT.getHostPageBaseURL(),
                new AsyncCallback<LoginInfo>() {
                    @Override
                    public void onFailure(Throwable error) {
                        handleError(error);
                    }

                    @Override
                    public void onSuccess(LoginInfo result) {
                        loginInfo = result;
                        if ((loginInfo.isLoggedIn() && loginInfo.getUser().getId() == point.getUserFK()) || point.isPublic()) {
                            loadChart(points, mainContentPanel);
                        } else {
                            loadLogin();
                        }
                    }

                });


    }

    private void loadChart(final List<Point> points, final ContentPanel p) {
        RecordedValueServiceAsync dataService;
        dataService = GWT.create(RecordedValueService.class);

        dataService.getLastRecordedDate(points, new AsyncCallback<Date>() {

            @Override
            public void onFailure(Throwable caught) {


            }

            @Override
            public void onSuccess(Date result) {

                final AnnotatedTimeLinePanel annotatedTimeLinePanel = new AnnotatedTimeLinePanel(false, Const.DEFAULT_CHART_NAME);
                // annotatedTimeLinePanel.setPoints(points);
                p.add(annotatedTimeLinePanel);
                p.setWidth(viewport.getWidth());
                p.setHeight(viewport.getHeight());
                annotatedTimeLinePanel.initChart();
                viewport.layout(true);
            }
        });
    }

    private void loadLogin() {

        Window.Location.replace(loginInfo.getLoginUrl());

    }

    private void handleError(Throwable error) {


        if (error instanceof NotLoggedInException) {
            Window.Location.replace(loginInfo.getLogoutUrl());
        } else if (error instanceof ObjectProtectionException) {
            Window.Location.replace(Const.PATH_OBJECT_PROTECTION_URL);
        } else {
            Window.alert(error.getMessage());

            // Window.Location.replace(Const.PATH_NIMBITS_HOME);
        }
    }
}
