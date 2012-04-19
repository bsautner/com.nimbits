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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.file.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.client.service.recordedvalues.*;
import com.nimbits.client.ui.helper.*;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.ui.*;
import org.vectomatic.dom.svg.utils.*;

import java.util.*;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/27/11
 * Time: 4:34 PM
 */
public class DiagramPanel extends LayoutContainer {

    private SVGImage image;
    private OMSVGSVGElement svg;
    private Map<EntityName, Entity> pointEntityMap = new HashMap<EntityName, Entity>(10);
    private Map<EntityName, Point> pointMap = new HashMap<EntityName, Point>(10);
    private Map<EntityName, Entity> diagrams = new HashMap<EntityName, Entity>(10);

    private final RecordedValueServiceAsync recordedValueService = GWT.create(RecordedValueService.class);
    //private final Set<EntityName> pointsInDiagram = new HashSet<EntityName>();
    private final Set<EntityName> diagramsInDiagram = new HashSet<EntityName>(10);
    private final Map<String, String> originalFill = new HashMap<String, String>(10);


    private final Entity diagram;



    public DiagramPanel(final File aDiagram, boolean showHeader) {
        final FlowPanel imagePanel = new FlowPanel();
        final String resourceUrl = Path.PATH_BLOB_SERVICE + '?' + Parameters.blobkey.getText() + '=' + aDiagram.getBlobKey();
        this.diagram = aDiagram;
        ContentPanel mainPanel = new ContentPanel();
        mainPanel.setFrame(true);
        mainPanel.add(imagePanel);
        mainPanel.setHeaderVisible(showHeader);
        add(mainPanel);


        final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, resourceUrl);
        requestBuilder.setCallback(new RequestCallback() {


            @Override
            public void onResponseReceived(final Request request, final Response response) {
                createSvg(response);
                createRefreshTimer();
            }

            private void createSvg(final Response response) {

                svg = OMSVGParser.parse(response.getText());
                svg.setAttribute("width", "100%");// String.valueOf(w));
                svg.setAttribute("height", "100%");//String.valueOf(adjH));
                image = new SVGImage(svg) {
                    protected void onAttach() {
                        super.onAttach();

                        final OMSVGRect viewBox = svg.getViewBox().getBaseVal();
                        if (viewBox.getWidth() == 0 || viewBox.getHeight() == 0) {
                            final OMSVGRect bBox = svg.getBBox();
                            bBox.assignTo(viewBox);
                        }

                        try {
                            addHandlers();
                            refreshDiagramValues();
                        } catch (NimbitsException e) {
                          FeedbackHelper.showError(e);
                        }





                    }
                };
                imagePanel.add(image);
                doLayout();
            }

            @Override
            public void onError(Request request, Throwable throwable) {

            }
        });

        try {
            requestBuilder.send();
        } catch (RequestException ignored) {

        }
    }


    private void createRefreshTimer() {
        final Timer updater = new Timer() {
            @Override
            public void run() {

                try {
                    refreshDiagramValues();
                } catch (NimbitsException e) {


                    FeedbackHelper.showError(e);
                }

            }
        };

        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
    }


    private void addHandler(final OMNode node) throws NimbitsException {

        if (node instanceof OMSVGTextElement) {
            processOMSVGTextElement((OMSVGTextElement) node);
        } else if (node instanceof OMSVGRectElement) {
            processOMSVGRectElement((OMSVGRectElement) node);
        } else if (node instanceof OMSVGPathElement) {
            processOMSVGPathElement((OMSVGPathElement) node);
        }


        if (node.hasChildNodes()) {
            for (OMNode c : node.getChildNodes()) {
                addHandler(c);
            }
        }

    }

    private void addHandlers() throws NimbitsException {

        for (OMNode node : svg.getChildNodes()) {
            addHandler(node);

        }
        loadAllPoints();

        getDiagramsUsedInDiagram();

    }

    private void getDiagramsUsedInDiagram()  {

        EntityServiceAsync serviceAsync = GWT.create(EntityService.class);

        serviceAsync.getEntityNameMap(EntityType.file, new AsyncCallback<Map<EntityName, Entity>>() {
            @Override
            public void onFailure(Throwable throwable) {
                GWT.log(throwable.getMessage(), throwable);
            }

            @Override
            public void onSuccess(Map<EntityName, Entity> stringEntityMap) {
                diagrams = stringEntityMap;
            }
        } );
    }

    private void loadAllPoints()  {

        EntityServiceAsync service = GWT.create(EntityService.class);
        service.getEntityNameMap(EntityType.point, new AsyncCallback<Map<EntityName, Entity>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Map<EntityName, Entity> result) {
                pointEntityMap = result;

            }
        });



    }



    private void refreshDiagramValues() throws NimbitsException {

        for (final OMNode node : svg.getChildNodes()) {
            refreshNodeValue(node);

        }


    }

    private void refreshNodeValue(final OMNode node) throws NimbitsException {
        if (node != null) {

            if (node instanceof OMSVGTextElement) {
                updateTextBoxValue((OMSVGTextElement) node);
            } else if (node instanceof OMSVGPathElement) {
                updatePathValue((OMSVGPathElement) node);
            } else if (node instanceof OMSVGRectElement) {
                updateRectValue((OMSVGRectElement) node);
            }


            if (node.hasChildNodes()) {

                for (OMNode c : node.getChildNodes()) {
                    refreshNodeValue(c);
                }

            }

        }

    }


    //Node processing

    //OMSVGTextElement

    private void processOMSVGTextElement(final OMSVGTextElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Parameters.point.getText());
        final String action = o.getAttribute(Parameters.action.getText());
        final String diagramNameParam = o.getAttribute(Parameters.diagram.getText());
        final String url = o.getAttribute(Parameters.url.getText());
        if (!Utils.isEmptyString(action)) {
           // final String[] actions = action.split(",");
            if (!Utils.isEmptyString(pointNameParam)) {
                EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);

                addTextPointActions(o, pointName);
            }
            if (!Utils.isEmptyString(diagramNameParam)) {
                EntityName diagramName = CommonFactoryLocator.getInstance().createName(diagramNameParam, EntityType.file);
                if (!diagramsInDiagram.contains(diagramName)) {
                    diagramsInDiagram.add(diagramName);
                }
                addNestedTextDiagramActions(o, diagramName);
            }
            if (!Utils.isEmptyString(url)) {
                addNestedTextUrlActions(o, url);
            }
        }


    }

    private void addTextPointActions(final OMSVGTextElement t, final EntityName name) {
        originalFill.put(t.getId(), t.getStyle().getSVGProperty(SVGConstants.CSS_FILL_VALUE));
        // com.google.gwt.user.client.Window.alert("Adding handler " + t.getId());

        t.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(final MouseDownEvent mouseDownEvent) {
                //   com.google.gwt.user.client.Window.alert("1");
                if (pointEntityMap.containsKey(name)) {
                    Entity entity = pointEntityMap.get(name);
                    try {
                        EntityOpenHelper.showEntity(entity);
                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }


                }
            }
        });

        t.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                        SVGConstants.CSS_YELLOW_VALUE);

            }
        });
        t.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {

                t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                        originalFill.get(t.getId()));
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }

    private void addNestedTextDiagramActions(final OMSVGTextElement t, final EntityName diagramName) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (diagrams.containsKey(diagramName)) {
                    EntityModel diagramModel = (EntityModel) diagrams.get(diagramName);
                    //diagramModel.setClientType(clientType);
                    try { Entity entity = EntityModelFactory.createEntity(diagramModel);

                        EntityOpenHelper.showEntity(entity);
                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }

                }
            }
        });


    }

    private void addNestedTextUrlActions(final OMSVGTextElement t, final String url) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                com.google.gwt.user.client.Window.open(url, "", "");

            }

        });


    }

    private void updateTextBoxValue(final OMSVGTextElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Parameters.point.getText());
        final String action = o.getAttribute(Parameters.action.getText());

        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            Entity entity = pointEntityMap.get(pointName);
            if (entity != null) {
                if (!Utils.isEmptyString(pointNameParam) && pointEntityMap.containsKey(pointName)) {
                    recordedValueService.getCurrentValue(entity, new AsyncCallback<Value>() {

                        @Override
                        public void onFailure(Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(Value result) {
                            try {
                                applyValueToTextNode(result, pointName, actions, o);
                            } catch (NimbitsException e) {
                                FeedbackHelper.showError(e);
                            }
                        }
                    });

                }
            }
        }
    }

    private void applyValueToTextNode(final Value result, final EntityName pointName, final String[] actions, final OMSVGTextElement o) throws NimbitsException {
        if (pointEntityMap != null && pointEntityMap.containsKey(pointName)) {

            final Entity entity = pointEntityMap.get(pointName);

            if (result != null) {
                if (pointMap.containsKey(entity.getName())) {

                    processTextNodeActions(pointMap.get(entity.getName()), result, actions, o);
                }
                EntityServiceAsync service = GWT.create(EntityService.class);
                service.getEntityByKey(entity.getKey(), EntityType.point,  new AsyncCallback<List<Entity>>() {

                    @Override
                    public void onFailure(Throwable caught) {

                    }

                    @Override
                    public void onSuccess(final List<Entity> p) {
                        try {

                            pointMap.put(entity.getName(), (Point) p.get(0));
                            processTextNodeActions((Point) p, result, actions, o);
                        } catch (NimbitsException e) {
                            FeedbackHelper.showError(e);
                        }

                    }


                });


            }
        }
    }
    private void processTextNodeActions(final Point p, final Value result, final String[] actions, final OMSVGTextElement o) {
        for (final String action : actions) {
            if (action.equals(Action.value.getCode())) {
                o.getElement().setInnerText(String.valueOf(Utils.roundDouble(result.getDoubleValue())));
            } else if (action.equals(Action.alert.getCode())) {
                if (result.getDoubleValue() <= p.getLowAlarm() && p.isLowAlarmOn()) {
                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                            SVGConstants.CSS_BLUE_VALUE);
                } else if (result.getDoubleValue() >= p.getHighAlarm() && p.isHighAlarmOn()) {
                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                            SVGConstants.CSS_RED_VALUE);
                } else {
                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                            originalFill.get(o.getId()));
                }
            } else if (action.equals(Action.idle.getCode()) && p.isIdleAlarmOn()) {
                DateWrapper n = new DateWrapper();
                long last = result.getTimestamp().getTime();
                long current = n.getTime();
                long max = p.getIdleSeconds() * 1000;
                long elapsed = current - last;

                if (elapsed > max) {
                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                            SVGConstants.CSS_ORANGE_VALUE);
                }
            } else if (action.equals(Action.onOff.getCode())) {
                if (result.getDoubleValue() == 0.0) {
                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                            SVGConstants.CSS_BLACK_VALUE);
                } else {
                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                            originalFill.get(o.getId()));
                }
            }

        }
    }
    //PATH

    private void processOMSVGPathElement(final OMSVGPathElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Parameters.point.getText());
        final String action = o.getAttribute(Parameters.action.getText());
        final String diagramNameParam = o.getAttribute(Parameters.diagram.getText());
        final String url = o.getAttribute(Parameters.url.getText());
        if (!Utils.isEmptyString(action)) {
           /// final String[] actions = action.split(",");
            if (!Utils.isEmptyString(pointNameParam)) {
                EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                addPathPointActions(o, pointName);
            }
            if (!Utils.isEmptyString(diagramNameParam)) {
                EntityName diagramName = CommonFactoryLocator.getInstance().createName(diagramNameParam, EntityType.file);
                if (!diagramsInDiagram.contains(diagramName)) {
                    diagramsInDiagram.add(diagramName);
                }
                addNestedPathDiagramActions(o, diagramName);
            }
            if (!Utils.isEmptyString(url)) {
                addPathUrlActions(o, url);
            }
        }


    }

    private void addPathPointActions(final OMSVGPathElement t, final EntityName pointName) {
        originalFill.put(t.getId(), t.getStyle().getSVGProperty(SVGConstants.CSS_FILL_VALUE));
        t.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (pointEntityMap.containsKey(pointName)) {
                    Entity entity =  pointEntityMap.get(pointName);
                    try {
                        EntityOpenHelper.showEntity(entity);
                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }


                }
            }
        });
        t.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                        SVGConstants.CSS_YELLOW_VALUE);

            }
        });
        t.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {

                t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                        originalFill.get(t.getId()));

            }
        });

    }

    private void addNestedPathDiagramActions(final OMSVGPathElement t, final EntityName diagramName) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(final MouseDownEvent mouseDownEvent) {
                if (diagrams.containsKey(diagramName)) {
                    EntityModel diagramModel = (EntityModel) diagrams.get(diagramName);
                    //diagramModel.setClientType(clientType);
                    Entity entity = null;
                    try {
                        entity = EntityModelFactory.createEntity(diagramModel);
                        EntityOpenHelper.showEntity(entity);
                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }


                }
            }
        });


    }

    private void updatePathValue(final OMSVGPathElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Parameters.point.getText());
        final String action = o.getAttribute(Parameters.action.getText());

        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            if (!Utils.isEmptyString(pointNameParam) && pointEntityMap.containsKey(pointName)) {

                recordedValueService.getCurrentValue(diagram, new AsyncCallback<Value>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        GWT.log(throwable.getMessage(), throwable);
                    }

                    @Override
                    public void onSuccess(Value result) {
                        applyValueToPathNode(result, pointName, actions, o);
                    }
                });

            }
        }
    }

    private void applyValueToPathNode(final Value result, final EntityName pointName, final String[] actions, final OMSVGPathElement o) {

        if (pointEntityMap != null) {
            final Entity entity = pointEntityMap.get(pointName);

            if (result != null) {

                EntityServiceAsync service = GWT.create(EntityService.class);
                service.getEntityByKey(entity.getKey(), EntityType.point , new AsyncCallback<List<Entity>>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        //auto generated
                    }

                    @Override
                    public void onSuccess(List<Entity> point) {
                        Point p = (Point) point.get(0);

                        for (String action : actions) {
                            if (action.equals(Action.value.getCode())) {
                                o.getElement().setInnerText(String.valueOf(result.getDoubleValue()));
                            } else if (action.equals(Action.alert.getCode())) {
                                if (result.getDoubleValue() <= p.getLowAlarm() && p.isLowAlarmOn()) {
                                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                            SVGConstants.CSS_RED_VALUE);
                                } else if (result.getDoubleValue() >= p.getHighAlarm() && p.isHighAlarmOn()) {
                                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                            SVGConstants.CSS_RED_VALUE);
                                } else {
                                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                            originalFill.get(o.getId()));
                                }
                            } else if (action.equals("idle") && p.isIdleAlarmOn()) {
                                DateWrapper n = new DateWrapper();
                                long last = result.getTimestamp().getTime();
                                long current = n.getTime();
                                long max = p.getIdleSeconds() * 1000;
                                long elapsed = current - last;

                                if (elapsed > max) {
                                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                            SVGConstants.CSS_GRAY_VALUE);
                                }
                            } else if (action.equals(Action.onOff.getCode())) {
                                if (result.getDoubleValue() == 0.0) {
                                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                            SVGConstants.CSS_BLACK_VALUE);
                                } else {
                                    o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                            originalFill.get(o.getId()));
                                }
                            }

                        }
                    }
                });

            }
        }
    }

    private void addPathUrlActions(final OMSVGPathElement t, final String url) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                com.google.gwt.user.client.Window.open(url, "", "");
            }

        });


    }

    //rect

    private void processOMSVGRectElement(OMSVGRectElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Parameters.point.getText());
        final String action = o.getAttribute(Parameters.action.getText());
        final String diagramNameParam = o.getAttribute(Parameters.diagram.getText());
        final String url = o.getAttribute(Parameters.url.getText());
        if (!Utils.isEmptyString(action)) {
//            final String[] actions = action.split(",");
            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
                addRectPointActions(o, pointName);
            }
            if (!Utils.isEmptyString(diagramNameParam)) {
                EntityName diagramName = CommonFactoryLocator.getInstance().createName(diagramNameParam, EntityType.file);
                if (!diagramsInDiagram.contains(diagramName)) {
                    diagramsInDiagram.add(diagramName);
                }
                addNestedRectDiagramActions(o, diagramName);
            }
            if (!Utils.isEmptyString(url)) {
                addRectUrlActions(o, url);
            }
        }
    }

    private void addRectPointActions(final OMSVGRectElement t, final EntityName pointName) {
        originalFill.put(t.getId(), t.getStyle().getSVGProperty(SVGConstants.CSS_FILL_VALUE));
        t.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (pointEntityMap.containsKey(pointName)) {

                    Entity entity = pointEntityMap.get(pointName);
                    try {
                        EntityOpenHelper.showEntity(entity);
                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }

                }
            }
        });
        t.addMouseOverHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent mouseOverEvent) {
                t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                        SVGConstants.CSS_YELLOW_VALUE);

            }
        });
        t.addMouseOutHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent mouseOutEvent) {

                t.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                        originalFill.get(t.getId()));
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

    }

    private void addNestedRectDiagramActions(final OMSVGRectElement t, final EntityName diagramName) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (diagrams.containsKey(diagramName)) {
                    EntityModel diagramModel = (EntityModel) diagrams.get(diagramName);
                    //diagramModel.setClientType(clientType);
                    Entity entity = null;
                    try {
                        entity = EntityModelFactory.createEntity(diagramModel);
                        EntityOpenHelper.showEntity(entity);

                    } catch (NimbitsException e) {
                        FeedbackHelper.showError(e);
                    }

                }
            }
        });
    }

    private void updateRectValue(final OMSVGRectElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Parameters.point.getText());
        final String action = o.getAttribute(Parameters.action.getText());

        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            if (!Utils.isEmptyString(pointNameParam) && pointEntityMap.containsKey(pointName)) {
                recordedValueService.getCurrentValue(diagram, new AsyncCallback<Value>() {

                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(Value result) {
                        applyValueToRectNode(result, pointName, actions, o);
                    }
                });

            }
        }
    }

    private void applyValueToRectNode(final Value result, EntityName pointName, final String[] actions, final OMSVGRectElement o) {


        if (result != null && pointEntityMap.containsKey(pointName)) {
            final Entity entity = pointEntityMap.get(pointName);

            EntityServiceAsync service = GWT.create(EntityService.class);
            service.getEntityByKey(entity.getKey(), EntityType.point, new AsyncCallback<List<Entity>>() {
                @Override
                public void onFailure(Throwable caught) {
                    //auto generated
                }

                @Override
                public void onSuccess(List<Entity> point) {
                    Point p = (Point) point.get(0);
                    for (String action : actions) {
                        if (action.equals(Action.value.getCode())) {
                            o.getElement().setInnerText(String.valueOf(result.getDoubleValue()));
                        } else if (action.equals(Action.alert.getCode())) {
                            if (result.getDoubleValue() <= p.getLowAlarm() && p.isLowAlarmOn()) {
                                o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                        SVGConstants.CSS_RED_VALUE);
                            } else if (result.getDoubleValue() >= p.getHighAlarm() && p.isHighAlarmOn()) {
                                o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                        SVGConstants.CSS_RED_VALUE);
                            } else {
                                o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                        originalFill.get(o.getId()));
                            }
                        } else if (action.equals(Parameters.idle.getText()) && p.isIdleAlarmOn()) {
                            DateWrapper n = new DateWrapper();
                            long last = result.getTimestamp().getTime();
                            long current = n.getTime();
                            long max = p.getIdleSeconds() * 1000;
                            long elapsed = current - last;

                            if (elapsed > max) {
                                o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                        SVGConstants.CSS_GRAY_VALUE);
                            }
                        } else if (action.equals(Action.onOff.getCode())) {
                            if (result.getDoubleValue() == 0.0) {
                                o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                        SVGConstants.CSS_BLACK_VALUE);
                            } else {
                                o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                        originalFill.get(o.getId()));
                            }
                        }

                    }
                }
            });

        }
    }

    private void addRectUrlActions(final OMSVGRectElement t, final String url) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                com.google.gwt.user.client.Window.open(url, "", "");
            }
        });

    }


}