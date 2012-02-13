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

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.entity.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/23/11
 * Time: 10:37 AM
 */
class CenterPanel extends NavigationEventProvider {

    final private Map<String, Entity> entities = new HashMap<String, Entity>();
    final private PointGridPanel grid = new PointGridPanel();
    private final Map<String, AnnotatedTimeLinePanel> lines = new HashMap<String, AnnotatedTimeLinePanel>();
    private ContentPanel bottom;

    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        grid.addEntityClickedListeners(new EntityClickedListener() {

            @Override
            public void onEntityClicked(final Entity entity) {

                addEntity(entity);


            }
        });

        grid.addValueEnteredListeners(new ValueEnteredListener() {

            @Override
            public void onValueEntered(final Entity entity, final Value value) {
                for (AnnotatedTimeLinePanel line : lines.values()) {
                    if (line.containsPoint(entity)) {
                        line.addValue(entity, value);
                    }
                }
            }
        });


        loadLayout();

    }

    private ToolBar toolbar() {
        ToolBar toolBar = new ToolBar();


        Button addChartButton = new Button("Add Chart");
        addChartButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.chart24()));
        addChartButton.setToolTip("Add another chart");

        addChartButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                final String name = (lines.size() == 0) ? Const.DEFAULT_CHART_NAME : "line" + lines.size() + 1;
                lines.put(name, createLine(name));
                addLinesToBottom();
            }
        });
        toolBar.add(addChartButton);


        Button removeButton = new Button("Hide Point");
        removeButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.delete()));
        removeButton.setToolTip("Remove Point from display");

        removeButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                List<Entity> selectedPoints = grid.getSelectedPoints();
                for (Entity px : selectedPoints) {
                    removePoint(px);
                }
                addLinesToBottom();
            }
        });
        toolBar.add(removeButton);


        Button saveButton = new Button("Save");
        saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveAll()));
        saveButton.setToolTip("Save checked rows");

        saveButton.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                try {
                    grid.saveSelectedPoints();
                } catch (NimbitsException e) {
                    Info.display("Error Saving", e.getMessage());
                }


            }
        });
        toolBar.add(saveButton);


        return toolBar;

    }

    private AnnotatedTimeLinePanel createLine(final String name) {
        final AnnotatedTimeLinePanel line = new AnnotatedTimeLinePanel(true, name);
        line.setSelected(true);
        line.addChartRemovedClickedListeners(new ChartRemovedListener() {
            @Override
            public void onChartRemovedClicked(String chartName) {
                lines.remove(chartName);

                addLinesToBottom();

            }
        });
        line.addListener(Events.OnClick, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent baseEvent) {
                for (AnnotatedTimeLinePanel l : lines.values()) {
                    l.setSelected(false);
                }
                line.setSelected(true);
            }
        });
        line.isSelected();
        for (AnnotatedTimeLinePanel l : lines.values()) {
            l.setSelected(false);
        }

        return line;
    }

    private boolean isOneLineSelected() {
        for (AnnotatedTimeLinePanel l : lines.values()) {
            if (l.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private void addLinesToBottom() {
        initBottomPanel();
        assert (lines.size() > 0);
        if (!isOneLineSelected() && lines.values().iterator().hasNext()) {
            lines.values().iterator().next().setSelected(true);
        }
        for (final AnnotatedTimeLinePanel l : lines.values()) {
            if (lines.size() == 1) {
                l.setSelected(true);
            }
            double w = 1.0 / (double) lines.size();
            l.initChart();
            //   l.refreshChart();
            bottom.add(l, new RowData(w, 1, new Margins(4)));
        }
        add(bottom, new FlowData(0));
        doLayout(true);
    }

    private void initBottomPanel() {
        if (bottom != null && getItems().contains(bottom)) {
            remove(bottom);
        }
        bottom = bottomPanel();
    }

    public void removePoint(final Entity entity) {
        for (AnnotatedTimeLinePanel line : lines.values()) {
            line.removePoint(entity);
            entities.remove(entity.getUUID());
        }
        grid.removePoint(entity);
    }

    private void loadLayout() {

        final ContentPanel panel = new ContentPanel();
        panel.setHeading("Data Channels");
        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));

        // panel.setSize(400, 300);
        panel.setFrame(true);
        panel.setCollapsible(true);
        panel.setHeight("100%");
        panel.add(grid, new RowData(1, 1, new Margins(4)));
        panel.setTopComponent(toolbar());
        add(panel, new FlowData(0));

        //  final private AnnotatedTimeLinePanel line1 = new AnnotatedTimeLinePanel(true);
        addBlankLineToBottom();

        // add(bottom, new FlowData(0));
        layout(true);

    }

    private void addBlankLineToBottom() {
        final AnnotatedTimeLinePanel line = createLine(Const.DEFAULT_CHART_NAME);
        lines.put(Const.DEFAULT_CHART_NAME, line);
        addLinesToBottom();
    }

    private ContentPanel bottomPanel() {
        final ContentPanel bottom = new ContentPanel();

        bottom.setLayout(new RowLayout(Style.Orientation.HORIZONTAL));
        bottom.setFrame(false);
        bottom.setCollapsible(true);
        bottom.setHeaderVisible(false);
        return bottom;
    }

    public void addEntity(final Entity entity) {

        for (Entity e : entity.getChildren()) {
            addEntity(e);
        }

        switch (entity.getEntityType()) {
            case user:
                break;
            case point:
                displayPoint(entity);
                break;
            case category:
                 break;
            case file:
                final String resourceUrl =
                        Const.PATH_BLOB_SERVICE +
                                "?" + Const.PARAM_BLOB_KEY + "=" + entity.getBlobKey();
                Window.open(resourceUrl, entity.getName().getValue(), "");

                break;
            case subscription:
                displaySubscription(entity);
                break;
            case userConnection:
                break;
        }



//
//            if (! line1.containsPoint(point)) {
//                line1.showEntityData(point);
//            }
//            else if (! line2.containsPoint(point))  {
//                line2.showEntityData(point);
//            }


    }

    private void displaySubscription(final Entity entity) {
        EntityServiceAsync service = GWT.create(EntityService.class);
        service.getSubscribedEntity(entity, new AsyncCallback<Entity>() {
            @Override
            public void onFailure(Throwable caught) {
                //auto generated
            }

            @Override
            public void onSuccess(Entity result) {
              addEntity(result);
            }
        });

    }

    private void displayPoint(final Entity entity) {
        if (!entities.containsKey(entity.getEntity())) {
            entities.put(entity.getEntity(), entity);

        }

        for (final AnnotatedTimeLinePanel line : lines.values()) {
            if (!line.containsPoint(entity) && line.isSelected()) {
                PointServiceAsync service = GWT.create(PointService.class);
                service.getPointByUUID(entity.getEntity(), new AsyncCallback<Point>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        //auto generated
                    }

                    @Override
                    public void onSuccess(Point result) {
                        line.addPoint(result);
                        grid.addPoint(entity);
                    }
                });

            }
        }


    }

//    public void addDiagram(final Diagram d) {
//        final int w = (bottom.getWidth() / 2);
//        final AnnotatedTimeLinePanel line = createLine(Const.DEFAULT_CHART_NAME);
//        final DiagramPanel diagramPanel = new DiagramPanel(d, true, w, bottom.getHeight());
//        diagramPanel.addPointClickedListeners(new EntityClickedListener() {
//
//            @Override
//            public void onPointClicked(final Point p){
//                showEntityData(p);
//            }
//
//        });
//        diagramPanel.addEntityClickedListeners(new EntityClickedListener() {
//
//            @Override
//            public void onDiagramClicked(Diagram d) {
//                bottom.remove(diagramPanel);
//                addDiagram(d);
//            }
//        });
//
//        diagramPanel.addDiagramRemovedClickedListeners(new DiagramRemovedListener() {
//            @Override
//            public void onDiagramRemovedClicked(Diagram diagram) {
//                bottom.remove(diagramPanel);
//                line.resize(bottom.getHeight(), bottom.getWidth());
//
//                line.setWidth(bottom.getWidth());
//                line.setHeight(bottom.getHeight());
//                bottom.remove(line);
//                bottom.add(line, new RowData(1, 1, new Margins(4)));
//                layout(true);
//            }
//        });
//        bottom.removeAll();
//        lines.clear();
//
//        line.setSelected(true);
//        lines.put(Const.DEFAULT_CHART_NAME, line);
//
//        bottom.add(diagramPanel, new RowData(w, 1, new Margins(4)));
//        bottom.add(line, new RowData(w, 1, new Margins(4)));
//        layout(true);
//    }
}
