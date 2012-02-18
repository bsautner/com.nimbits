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

package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.nimbits.client.enums.ClientType;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.service.entity.EntityService;
import com.nimbits.client.service.entity.EntityServiceAsync;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;
import com.nimbits.shared.Utils;
import org.vectomatic.dom.svg.*;
import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.utils.OMSVGParser;
import org.vectomatic.dom.svg.utils.SVGConstants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/27/11
 * Time: 4:34 PM
 */
public class DiagramPanel extends NavigationEventProvider {
    //1.0


    // private static final Icons ICONS = GWT.create(Icons.class);
    private SVGImage image;
    private OMSVGSVGElement svg;
    private final ContentPanel mainPanel = new ContentPanel();
    private Map<EntityName, Point> points = new HashMap<EntityName, Point>();
    private Map<EntityName, Entity> diagrams = new HashMap<EntityName, Entity>();

    private final boolean readOnly;
    private final RecordedValueServiceAsync recordedValueService = GWT.create(RecordedValueService.class);
    private final Set<EntityName> pointsInDiagram = new HashSet<EntityName>();
    private final Set<EntityName> diagramsInDiagram = new HashSet<EntityName>();
    private final Map<String, String> originalFill = new HashMap<String, String>();

    public Entity getDiagram() {
        return diagram;
    }

    private final Entity diagram;

    private final ClientType clientType;

    public DiagramPanel(final Entity aDiagram, boolean showHeader, final int w, final int h) {
        //final ToolBar toolbar = createToolbar(aDiagram);

        final FlowPanel imagePanel = new FlowPanel();
        final String resourceUrl = Const.PATH_BLOB_SERVICE + "?" + Const.PARAM_BLOB_KEY + "=" + aDiagram.getBlobKey();
        this.diagram = aDiagram;
        this.readOnly = aDiagram.isReadOnly();
        this.clientType = ClientType.other;

        //  mainPanel.setTopComponent(toolbar);

        mainPanel.setFrame(true);
        mainPanel.add(imagePanel);


        mainPanel.setHeaderVisible(showHeader);
        mainPanel.getHeader().addTool(
                maximizeToolbarButton());
        mainPanel.getHeader().addTool(
                closeToolbarButton());

        add(mainPanel);


        final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, resourceUrl);
        requestBuilder.setCallback(new RequestCallback() {


            @Override
            public void onResponseReceived(final Request request, final Response response) {
                createSvg(response);
                createRefreshTimer();
            }

            private void createSvg(final Response response) {
                // final int adjH = h - 25;
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

                        //  svg.getWidth().getBaseVal().newValueSpecifiedUnits(Style.Unit.PX, w);
                        //  svg.getHeight().getBaseVal().newValueSpecifiedUnits(Style.Unit.PX, adjH);//- createToolbar.getSize().height);
                        try {
                            addHandlers();
                            refreshDiagramValues();

                        } catch (NimbitsException ignored) {


                        }
                        //refreshDiagramValues();


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

    private ToolButton maximizeToolbarButton() {
        return new ToolButton("x-tool-maximize",
                new SelectionListener<IconButtonEvent>() {
                    boolean isMax;

                    @Override
                    public void componentSelected(final IconButtonEvent ce) {
                        final Window window = new Window();

                        final DiagramPanel panel = new DiagramPanel(getDiagram(), false, 800, 800);


                        window.add(panel);
                        window.setWidth(800);
                        window.setHeight(800);

                        window.show();


                    }
                });
    }

    private ToolButton closeToolbarButton() {
        return new ToolButton("x-tool-close",
                new SelectionListener<IconButtonEvent>() {
                    boolean isMax;

                    @Override
                    public void componentSelected(final IconButtonEvent ce) {

                        //notifyDiagramRemovedListener(getDiagram());
                    }
                });
    }
//    public void setDiagram(final Diagram aDiagram) {
//
//    }

    private void createRefreshTimer() {
        Timer updater = new Timer() {
            @Override
            public void run() {
                try {
                    refreshDiagramValues();
                } catch (NimbitsException e) {
                    GWT.log(e.getMessage());
                }
            }
        };

        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
    }

//    private ToolBar createToolbar(final Diagram diagram) {
//        ToolBar toolBar = new ToolBar();
//        Button refresh = new Button();
//
//        refresh.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh2()));
//
//        refresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
//            @Override
//            public void handleEvent(BaseEvent be) {
//                try {
//                    refreshDiagramValues();
//                } catch (NimbitsException e) {
//
//                }
//
//            }
//        });
//
//        Button props = new Button();
//
//        props.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.edit()));
//
//        props.addListener(Events.OnClick, new Listener<BaseEvent>() {
//            @Override
//            public void handleEvent(BaseEvent be) {
//                DiagramPropertyPanel dp = new DiagramPropertyPanel(diagram, readOnly);
//                final Window w = new Window();
//                w.setWidth(500);
//                w.setHeight(400);
//                w.setHeading(diagram.getName() + " Properties");
//                w.add(dp);
//                w.show();
//                dp.addDiagramDeletedListeners(new DiagramDeletedListener() {
//
//
//                    @Override
//                    public void onDiagramDeleted(Diagram c, boolean readOnly) throws NimbitsException {
//                        w.hide();
//                        notifyDiagramDeletedListener(c, readOnly);
//                    }
//                });
//
//
//            }
//        });
//
//        toolBar.add(new SeparatorToolItem());
//        toolBar.add(refresh);
//        toolBar.add(props);
//
//        return toolBar;
//
//    }

    private void addHandler(final OMNode node) {

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
        getPointsUsedInDiagram();

        getDiagramsUsedInDiagram();

    }

    private void getDiagramsUsedInDiagram() throws NimbitsException {

        EntityServiceAsync serviceAsync = GWT.create(EntityService.class);

        serviceAsync.getEntityNameMap(EntityType.file, new AsyncCallback<Map<EntityName, Entity>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(Map<EntityName, Entity> stringEntityMap) {
                diagrams = stringEntityMap;
            }
        } );
    }

    private void getPointsUsedInDiagram() throws NimbitsException {
        //TODO
//        PointServiceAsync serviceAsync = GWT.create(PointService.class);
//
//        serviceAsync.getEntityNamePointMap(EntityType.point, new AsyncCallback<Map<EntityName, Entity>>() {
//            @Override
//            public void onFailure(Throwable throwable) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public void onSuccess(Map<EntityName, Entity> stringEntityMap) {
//                points = stringEntityMap;
//            }
//        } );
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
                try {
                    for (OMNode c : node.getChildNodes()) {
                        refreshNodeValue(c);
                    }
                } catch (NimbitsException e) {
                    GWT.log(e.getMessage(), e);
                }
            }

        }

    }

    public void resizePanel(final int defaultHeight, final int defaultWidth) {
        mainPanel.setWidth(defaultWidth);
        mainPanel.setHeight(defaultHeight);
        svg.getWidth().getBaseVal().newValueSpecifiedUnits(Style.Unit.PX, defaultWidth);
        svg.getHeight().getBaseVal().newValueSpecifiedUnits(Style.Unit.PX, defaultHeight - 25);//- createToolbar.getSize().height);
        //To change body of created methods use File | Settings | File Templates.
    }

    //Node processing

    //OMSVGTextElement

    private void processOMSVGTextElement(final OMSVGTextElement o) {
        final String pointNameParam = o.getAttribute(Const.PARAM_POINT);
        final String action = o.getAttribute(Const.PARAM_ACTION);
        final String diagramNameParam = o.getAttribute(Const.PARAM_DIAGRAM);
        final String url = o.getAttribute(Const.PARAM_URL);
        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            if (!Utils.isEmptyString(pointNameParam)) {
                EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
                if (!pointsInDiagram.contains(pointName)) {

                    pointsInDiagram.add(pointName);
                }
                addTextPointActions(o, pointName, actions);
            }
            if (!Utils.isEmptyString(diagramNameParam)) {
                EntityName diagramName = CommonFactoryLocator.getInstance().createName(diagramNameParam);
                if (!diagramsInDiagram.contains(diagramName)) {
                    diagramsInDiagram.add(diagramName);
                }
                addNestedTextDiagramActions(o, diagramName, actions);
            }
            if (!Utils.isEmptyString(url)) {
                addNestedTextUrlActions(o, url, actions);
            }
        }


    }

    private void addTextPointActions(final OMSVGTextElement t, final EntityName pointName, final String[] actions) {
        originalFill.put(t.getId(), t.getStyle().getSVGProperty(SVGConstants.CSS_FILL_VALUE));
        // com.google.gwt.user.client.Window.alert("Adding handler " + t.getId());

            t.addMouseDownHandler(new MouseDownHandler() {

                @Override
                public void onMouseDown(final MouseDownEvent mouseDownEvent) {
                    //   com.google.gwt.user.client.Window.alert("1");
                    if (points.containsKey(pointName)) {
                        //  com.google.gwt.user.client.Window.alert(pointName);
                        PointModel p = (PointModel) points.get(pointName);

                        Entity entity = EntityModelFactory.createEntity(null,p, pointName);
                        notifyEntityClickedListener(entity);


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

    private void addNestedTextDiagramActions(final OMSVGTextElement t, final EntityName diagramName, final String[] actions) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (diagrams.containsKey(diagramName)) {
                    EntityModel diagramModel = (EntityModel) diagrams.get(diagramName);
                    //diagramModel.setClientType(clientType);
                    Entity entity = EntityModelFactory.createEntity(diagramModel);
                    notifyEntityClickedListener(entity);

                }
            }
        });


    }

    private void addNestedTextUrlActions(final OMSVGTextElement t, final String url, final String[] actions) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {

                String target;
                if (actions.length > 0) {
                    target = actions[0];
                } else {
                    target = Const.PARAM_SELF;
                }

                notifyUrlClickedListener(url, target);

            }

        });


    }

    private void updateTextBoxValue(final OMSVGTextElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Const.PARAM_POINT);
        final String action = o.getAttribute(Const.PARAM_ACTION);

        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
            if (!Utils.isEmptyString(pointNameParam) && points.containsKey(pointName)) {
                recordedValueService.getCurrentValue(diagram, new AsyncCallback<Value>() {

                    @Override
                    public void onFailure(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(Value result) {
                        applyValueToTextNode(result, pointName, actions, o);
                    }
                });

            }
        }
    }

    private void applyValueToTextNode(final Value result, final EntityName pointName, final String[] actions, final OMSVGTextElement o) {
        if (points != null) {

            final Point p = points.get(pointName);

            if (result != null) {


                for (final String action : actions) {
                    if (action.equals(Const.ACTION_VALUE)) {
                        o.getElement().setInnerText(String.valueOf(Utils.roundDouble(result.getNumberValue())));
                    } else if (action.equals(Const.ACTION_ALERT)) {
                        if (result.getNumberValue() <= p.getLowAlarm() && p.isLowAlarmOn()) {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    SVGConstants.CSS_BLUE_VALUE);
                        } else if (result.getNumberValue() >= p.getHighAlarm() && p.isHighAlarmOn()) {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    SVGConstants.CSS_RED_VALUE);
                        } else {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    originalFill.get(o.getId()));
                        }
                    } else if (action.equals(Const.ACTION_IDLE) && p.isIdleAlarmOn()) {
                        DateWrapper n = new DateWrapper();
                        long last = result.getTimestamp().getTime();
                        long current = n.getTime();
                        long max = p.getIdleSeconds() * 1000;
                        long elapsed = current - last;

                        if (elapsed > max) {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    SVGConstants.CSS_ORANGE_VALUE);
                        }
                    } else if (action.equals(Const.ACTION_ONOFF)) {
                        if (result.getNumberValue() == 0.0) {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    SVGConstants.CSS_BLACK_VALUE);
                        } else {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    originalFill.get(o.getId()));
                        }
                    }

                }
            }
        }
    }

    //PATH

    private void processOMSVGPathElement(final OMSVGPathElement o) {
        final String pointNameParam = o.getAttribute(Const.PARAM_POINT);
        final String action = o.getAttribute(Const.PARAM_ACTION);
        final String diagramNameParam = o.getAttribute(Const.PARAM_DIAGRAM);
        final String url = o.getAttribute(Const.PARAM_URL);
        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            if (!Utils.isEmptyString(pointNameParam)) {
                EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
                if (!pointsInDiagram.contains(pointName)) {
                    pointsInDiagram.add(pointName);
                }
                addPathPointActions(o, pointName, actions);
            }
            if (!Utils.isEmptyString(diagramNameParam)) {
                EntityName diagramName = CommonFactoryLocator.getInstance().createName(diagramNameParam);
                if (!diagramsInDiagram.contains(diagramName)) {
                    diagramsInDiagram.add(diagramName);
                }
                addNestedPathDiagramActions(o, diagramName, actions);
            }
            if (!Utils.isEmptyString(url)) {
                addPathUrlActions(o, url, actions);
            }
        }


    }

    private void addPathPointActions(final OMSVGPathElement t, final EntityName pointName, final String[] actions) {
        originalFill.put(t.getId(), t.getStyle().getSVGProperty(SVGConstants.CSS_FILL_VALUE));
        t.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (points.containsKey(pointName)) {
                    PointModel p = (PointModel) points.get(pointName);
                    Entity entity = EntityModelFactory.createEntity(null, p, pointName);
                    notifyEntityClickedListener(entity);

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

    private void addNestedPathDiagramActions(final OMSVGPathElement t, final EntityName diagramName, final String[] actions) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(final MouseDownEvent mouseDownEvent) {
                if (diagrams.containsKey(diagramName)) {
                    EntityModel diagramModel = (EntityModel) diagrams.get(diagramName);
                    //diagramModel.setClientType(clientType);
                    Entity entity = EntityModelFactory.createEntity(diagramModel);
                    notifyEntityClickedListener(entity);

                }
            }
        });


    }

    private void updatePathValue(final OMSVGPathElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Const.PARAM_POINT);
        final String action = o.getAttribute(Const.PARAM_ACTION);

        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
            if (!Utils.isEmptyString(pointNameParam) && points.containsKey(pointName)) {

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

        if (points != null) {
            final Point p = points.get(pointName);

            if (result != null) {


                for (String action : actions) {
                    if (action.equals(Const.ACTION_VALUE)) {
                        o.getElement().setInnerText(String.valueOf(result.getNumberValue()));
                    } else if (action.equals(Const.ACTION_ALERT)) {
                        if (result.getNumberValue() <= p.getLowAlarm() && p.isLowAlarmOn()) {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    SVGConstants.CSS_RED_VALUE);
                        } else if (result.getNumberValue() >= p.getHighAlarm() && p.isHighAlarmOn()) {
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
                    } else if (action.equals(Const.ACTION_ONOFF)) {
                        if (result.getNumberValue() == 0.0) {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    SVGConstants.CSS_BLACK_VALUE);
                        } else {
                            o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                    originalFill.get(o.getId()));
                        }
                    }

                }
            }
        }
    }

    private void addPathUrlActions(final OMSVGPathElement t, final String url, final String[] actions) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {

                String target;
                if (actions.length > 0) {
                    target = actions[0];
                } else {
                    target = Const.PARAM_SELF;
                }

                notifyUrlClickedListener(url, target);

            }

        });


    }

    //rect

    private void processOMSVGRectElement(OMSVGRectElement o) {
        final String pointNameParam = o.getAttribute(Const.PARAM_POINT);
        final String action = o.getAttribute(Const.PARAM_ACTION);
        final String diagramNameParam = o.getAttribute(Const.PARAM_DIAGRAM);
        final String url = o.getAttribute(Const.PARAM_URL);
        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            if (!Utils.isEmptyString(pointNameParam)) {
                final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
                if (!pointsInDiagram.contains(pointName)) {
                    pointsInDiagram.add(pointName);
                }
                addRectPointActions(o, pointName, actions);
            }
            if (!Utils.isEmptyString(diagramNameParam)) {
                EntityName diagramName = CommonFactoryLocator.getInstance().createName(diagramNameParam);
                if (!diagramsInDiagram.contains(diagramName)) {

                    diagramsInDiagram.add(diagramName);
                }
                addNestedRectDiagramActions(o, diagramName, actions);
            }
            if (!Utils.isEmptyString(url)) {
                addRectUrlActions(o, url, actions);
            }
        }


    }

    private void addRectPointActions(final OMSVGRectElement t, final EntityName pointName, final String[] actions) {
        originalFill.put(t.getId(), t.getStyle().getSVGProperty(SVGConstants.CSS_FILL_VALUE));
        t.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (points.containsKey(pointName)) {
                    PointModel p = (PointModel) points.get(pointName);

                    Entity entity = EntityModelFactory.createEntity(null, p, pointName);
                  notifyEntityClickedListener(entity);

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

    private void addNestedRectDiagramActions(final OMSVGRectElement t, final EntityName diagramName, final String[] actions) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                if (diagrams.containsKey(diagramName)) {
                    EntityModel diagramModel = (EntityModel) diagrams.get(diagramName);
                    //diagramModel.setClientType(clientType);
                    Entity entity = EntityModelFactory.createEntity(diagramModel);
                    notifyEntityClickedListener(entity);
                }
            }
        });
    }

    private void updateRectValue(final OMSVGRectElement o) throws NimbitsException {
        final String pointNameParam = o.getAttribute(Const.PARAM_POINT);
        final String action = o.getAttribute(Const.PARAM_ACTION);

        if (!Utils.isEmptyString(action)) {
            final String[] actions = action.split(",");
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam);
            if (!Utils.isEmptyString(pointNameParam) && points.containsKey(pointName)) {
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

    private void applyValueToRectNode(Value result, EntityName pointName, String[] actions, OMSVGRectElement o) {
        final Point p = points.get(pointName);

        if (result != null) {


            for (String action : actions) {
                if (action.equals(Const.ACTION_VALUE)) {
                    o.getElement().setInnerText(String.valueOf(result.getNumberValue()));
                } else if (action.equals(Const.ACTION_ALERT)) {
                    if (result.getNumberValue() <= p.getLowAlarm() && p.isLowAlarmOn()) {
                        o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                SVGConstants.CSS_RED_VALUE);
                    } else if (result.getNumberValue() >= p.getHighAlarm() && p.isHighAlarmOn()) {
                        o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                SVGConstants.CSS_RED_VALUE);
                    } else {
                        o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                originalFill.get(o.getId()));
                    }
                } else if (action.equals(Const.PARAM_IDLE) && p.isIdleAlarmOn()) {
                    DateWrapper n = new DateWrapper();
                    long last = result.getTimestamp().getTime();
                    long current = n.getTime();
                    long max = p.getIdleSeconds() * 1000;
                    long elapsed = current - last;

                    if (elapsed > max) {
                        o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                SVGConstants.CSS_GRAY_VALUE);
                    }
                } else if (action.equals(Const.ACTION_ONOFF)) {
                    if (result.getNumberValue() == 0.0) {
                        o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                SVGConstants.CSS_BLACK_VALUE);
                    } else {
                        o.getStyle().setSVGProperty(SVGConstants.CSS_FILL_VALUE,
                                originalFill.get(o.getId()));
                    }
                }

            }
        }
    }

    private void addRectUrlActions(final OMSVGRectElement t, final String url, final String[] actions) {
        t.addMouseDownHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {

                String target;
                if (actions.length > 0) {
                    target = actions[0];
                } else {
                    target = Const.PARAM_SELF;
                }

                notifyUrlClickedListener(url, target);

            }

        });


    }


}