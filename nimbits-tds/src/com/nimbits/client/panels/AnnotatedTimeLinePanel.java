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

import com.extjs.gxt.ui.client.dnd.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import static com.google.gwt.user.client.Window.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.visualization.client.AbstractDataTable.*;
import com.google.gwt.visualization.client.*;
import com.google.gwt.visualization.client.visualizations.*;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.datapoints.*;
import com.nimbits.client.service.recordedvalues.*;
import com.nimbits.shared.*;

import java.util.*;

public class AnnotatedTimeLinePanel extends NavigationEventProvider {
    private final DateTimeFormat fmt = DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME);

    private AnnotatedTimeLine line;
    private ContentPanel mainPanel;
    private DataTable dataTable = null;

    private final Map<EntityName, Entity> points = new HashMap<EntityName, Entity>();
    private final Map<EntityName, List<Value>> valueMap = new HashMap<EntityName, List<Value>>();
    private final TextField endDateSelector = new TextField();
    private final TextField startDateSelector = new TextField();
    private Timespan timespan;
    private boolean headerVisible;
    private final String name;
    private boolean selected;

    public AnnotatedTimeLinePanel(final boolean showHeader, final String name) {
        this.headerVisible = showHeader;

        this.name = name;
    }

    //data

    private void addPointDataToTable(final Entity entity, final List<Value> values) {
        int PointColumn;

        boolean found = false;


        removePointDataFromTable(CommonFactoryLocator.getInstance().createName(Const.DEFAULT_EMPTY_COL));

        int r = dataTable.getNumberOfColumns();
        int CurrentRow = dataTable.getNumberOfRows();
        PointColumn = dataTable.getNumberOfColumns();

        for (int i = 0; i < r; i++) {
            String s = dataTable.getColumnLabel(i);
            if (s.equals(entity.getName().getValue())) {
                PointColumn = i;
                found = true;
                break;
            }
        }


        if (!found) {
            dataTable.addColumn(ColumnType.NUMBER, entity.getName().getValue());
            dataTable.addColumn(ColumnType.STRING, "title" + r);
            dataTable.addColumn(ColumnType.STRING, "text" + r);
        }

        if (values != null) {
            if (! valueMap.containsKey(entity.getName())) {
                valueMap.get(entity.getName()).addAll(values);
            }
            else {
                valueMap.put(entity.getName(), values);
            }
            for (Value v : values) {

//                points.get(entity.getName()).getValues().add(v);

                dataTable.addRow();
                dataTable.setValue(CurrentRow, 0, v.getTimestamp());
                dataTable.setValue(CurrentRow, PointColumn, v.getNumberValue());

                String note = v.getNote();
                String name =entity.getName().getValue();

                if (Utils.isEmptyString(note)) {
                    note = null;
                    name = null;
                }

                //note = null;
                dataTable.setValue(CurrentRow, PointColumn + 2, note);
                dataTable.setValue(CurrentRow, PointColumn + 1, name);

                CurrentRow++;
            }
        }
    }

    private void removePointDataFromTable(final EntityName pointName) {
        int r = dataTable.getNumberOfColumns();
        for (int i = 0; i < r; i++) {
            String s = dataTable.getColumnLabel(i);
            if (s.equals(pointName.getValue())) {
                dataTable.removeColumns(i, i + 2);
                break;
            }
        }

    }

    //end data


    public boolean containsPoint(final Entity point) {
        return points.containsKey(point.getName());
    }

    public void addValue(final Entity entity, final Value value) {
        if (timespan != null) {
            Date end = (timespan.getEnd().getTime() > value.getTimestamp().getTime()) ? value.getTimestamp() : timespan.getEnd();
            Date start = (timespan.getStart().getTime() < value.getTimestamp().getTime()) ? value.getTimestamp() : timespan.getStart();
            if (value.getTimestamp().getTime() < start.getTime()) {
                start = value.getTimestamp();
            }
            if (value.getTimestamp().getTime() > end.getTime()) {
                end = value.getTimestamp();
            }
            this.timespan = TimespanModelFactory.createTimespan(start, end);

        } else {
            this.timespan =TimespanModelFactory.createTimespan(value.getTimestamp(), new Date());


        }
        startDateSelector.setValue(fmt.format(this.timespan.getStart()));
        endDateSelector.setValue(fmt.format(this.timespan.getEnd()));
        addPointDataToTable(entity, Arrays.asList(value));

        drawChart();
    }

    private void drawChart() {
        layout();
        line.draw(dataTable, createOptions());
    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);

        mainPanel = new ContentPanel();
        mainPanel.setBodyBorder(true);
        mainPanel.setHeaderVisible(headerVisible);

        mainPanel.setFrame(true);
        mainPanel.setTopComponent(toolbar());
        //   mainPanel.setLayout(new FillLayout());
        mainPanel.setHeight(400);
        if (headerVisible) {
            mainPanel.getHeader().addTool(
                    maximizeToolbarButton());
            mainPanel.getHeader().addTool(
                    closeToolbarButton());
        }
        setDropTarget(mainPanel);
        add(mainPanel);
        initChart();
        //  layout(true);
    }

    private ToolBar toolbar() {
        final ToolBar toolBar = new ToolBar();


        final Button startDateMenu = new Button();
        startDateMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.calendar()));


        startDateSelector.setSelectOnFocus(false);
        if (timespan != null) {
            startDateSelector.setValue(fmt.format(timespan.getStart()));
        }
        startDateSelector.setToolTip("Start Date");

        startDateSelector.addListener(Events.KeyPress, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == 13) {
                    try {
                        refreshChart();
                    } catch (NimbitsException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });


        if (timespan != null) {
            endDateSelector.setValue(fmt.format(timespan.getEnd()));
        }
        endDateSelector.setSelectOnFocus(false);
        endDateSelector.setToolTip("End Date");
        endDateSelector.addListener(Events.KeyPress, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == 13) {
                    try {
                        refreshChart();
                    } catch (NimbitsException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        });


        final Button refresh = new Button();
        refresh.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh2()));


        final Button export = new Button();
        export.setText("Export and Report");
        export.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.plugin()));
        export.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent be) {
                if (points.size() > 0) {
                    Window w = new Window();
                    w.setHeading("Export Options");
                    w.setWidth(500);
                    w.setHeight(300);
                    w.add((new ExportPanel(points, valueMap)));
                    w.show();
                } else {
                    alert("Please select a point, and load some data into the chart. Then you can use this button");
                }
            }
        });


        toolBar.add(startDateSelector);
        // toolBar.add(startDateMenu);
        //  toolBar.add(new SeparatorToolItem());

        toolBar.add(endDateSelector);
        //   toolBar.add(new SeparatorToolItem());

        refresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent be) {
                try {
                    refreshChart();
                } catch (NimbitsException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        toolBar.add(refresh);

        toolBar.add(new SeparatorToolItem());

        final NumberField min = new NumberField();
        final NumberField max = new NumberField();
        Label minY = new Label("MinY:");
        Label maxY = new Label("MaxY:");
        min.setWidth(30);
        max.setWidth(30);

        min.setValue(0);
        max.setValue(100);

        toolBar.add(minY);
        toolBar.add(min);
        toolBar.add(maxY);
        toolBar.add(max);


        Button refreshRange = new Button();
        refreshRange.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh2()));
        refreshRange.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                Options options = Options.create();
                options.setDisplayAnnotations(true);
                options.setWindowMode(WindowMode.OPAQUE);
                options.setAllowRedraw(true);
                options.setDisplayRangeSelector(true);

                int mn = min.getValue().intValue();
                int mx = max.getValue().intValue();

                options.setMax(mx);
                options.setMin(mn);


                line.draw(dataTable, options);
            }
        });
        toolBar.add(refreshRange);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(export);

        return toolBar;
    }

    public void initChart() {
        Runnable onLoadCallback = new Runnable() {
            @Override
            public void run() {
                if (line != null) {
                    mainPanel.remove(line);
                    line = null;
                }

                dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.DATETIME, Const.WORD_DATE);
//                line = new AnnotatedTimeLine(dataTable, createOptions(), w + "px",
//                        (h - heightMod) + "px");
                line = new AnnotatedTimeLine(dataTable, createOptions(), "100%", "100%");
                //  	line.setVisibleChartRange(startDate, endDate);
                mainPanel.add(line);

                addEmptyDataToTable();
                //emptyPanel.setLayout(new FillLayout());
                //  mainPanel.add(h);
                layout();
                if (points != null && points.size() > 0) {
                    try {
                        refreshChart();
                    } catch (NimbitsException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        };

        VisualizationUtils.loadVisualizationApi(onLoadCallback,
                AnnotatedTimeLine.PACKAGE);
    }

    private void addEmptyDataToTable() {
        dataTable.addColumn(ColumnType.NUMBER, Const.DEFAULT_EMPTY_COL);
        dataTable.addColumn(ColumnType.STRING, "title0");
        dataTable.addColumn(ColumnType.STRING, "text0");
    }

    private void refreshChart() throws NimbitsException {

            timespan = TimespanServiceClientImpl.createTimespan(startDateSelector.getValue().toString(), endDateSelector.getValue().toString());
            if (line != null && timespan != null) {
                line.setVisibleChartRange(timespan.getStart(), timespan.getEnd());
                dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.DATETIME, "Date");

                for (EntityName pointName : points.keySet()) {
                    addPointToChart(points.get(pointName));
                }
            }


    }

    public void addPoint(Entity point) {
        addPointToChart(point);
    }

    private void addPointToChart(final Entity point) {
        if (!points.containsKey(point.getName())) {
            points.put(point.getName(), point);
        }

        final int start = 0;
        final int end = 1000;

        if (timespan == null) {
            loadValuesThatExist(point);
        } else {
            loadMemCache(point);
            loadDataSegment(point, start, end);
        }
    }

    private void loadValuesThatExist(final Entity p) {
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);

        dataService.getTopDataSeries(p, 100, new Date(), new AsyncCallback<List<Value>>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
            }

            @Override
            public void onSuccess(final List<Value> result) {
                Value oldest, newest;

                if (result.size() > 0) {
                    oldest = result.get(result.size() - 1);


                    newest = result.get(0);


                    timespan = TimespanModelFactory.createTimespan(oldest.getTimestamp(), newest.getTimestamp());
                    setTimespan(timespan);
                }


                addPointDataToTable(p, result);

                drawChart();


                // box.close();
            }
        });
    }

    public void setTimespan(Timespan ts) {
        this.timespan = ts;
        this.startDateSelector.setValue(fmt.format(ts.getStart()));
        this.endDateSelector.setValue(fmt.format(ts.getEnd()));
    }

    private void loadDataSegment(final Entity p, final int start, final int end) {
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        final MessageBox box = MessageBox.wait("Progress",
                "Loading " + p.getName().getValue() + " archived values " + start + " to " + end, "Loading...");
        box.show();
        //   Timespan timespan = new TimespanModel(startDate, endDate);
        dataService.getPieceOfDataSegment(p, timespan, start, end, new AsyncCallback<List<Value>>() {
            @Override
            public void onFailure(final Throwable caught) {
                box.close();
            }

            @Override
            public void onSuccess(final List<Value> result) {
                addPointDataToTable(p, result);
                if (result.size() > 0) {
                    loadDataSegment(p, end + 1, end + 1000);
                } else {
                    drawChart();
                }

                box.close();
            }
        });
    }

    private void loadMemCache(final Entity p) {
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        final MessageBox box = MessageBox.wait("Progress",
                "Loading Buffered Data", "Loading...");
        box.show();
        //   Timespan timespan = new TimespanModel(startDate, endDate);
        dataService.getCache(p, new AsyncCallback<List<Value>>() {
            @Override
            public void onFailure(final Throwable caught) {
                box.close();
            }

            @Override
            public void onSuccess(final List<Value> result) {
                addPointDataToTable(p, result);

                box.close();
            }
        });


    }

    void resize(final int h, final int w) {
        mainPanel.setHeight(h);
        mainPanel.setWidth(w);
        line.setHeight("100%");
        line.setWidth("100%");
    }

    private void setDropTarget(final Component container) {
        //    DropTarget target = new DropTarget(container) {
        new DropTarget(container) {
            @Override
            protected void onDragDrop(final DNDEvent event) {
                super.onDragDrop(event);
                List<TreeStoreModel> t = event.getData();

                for (final TreeStoreModel a : t) {
                    final GxtModel p = (GxtModel) a.getModel();
                    addPointToChart(p.getBaseEntity());
                    final PointServiceAsync pointService = GWT.create(PointService.class);

                }
            }
        };
    }

    private Options createOptions() {
        Options options = Options.create();
        options.setDisplayAnnotations(true);
        options.setWindowMode(WindowMode.OPAQUE);
        options.setAllowRedraw(true);
        options.setDisplayRangeSelector(true);

        //options.setDisplayAnnotationsFilter(arg0)
        return options;
    }

    public void removePoint(Entity entity) {
        removePointDataFromTable(entity.getName());
        if (points.containsKey(entity.getName())) {
            points.remove(entity.getName());
        }
        if (points.size() == 0) {
            removePointDataFromTable(CommonFactoryLocator.getInstance().createName(Const.DEFAULT_EMPTY_COL));
            addEmptyDataToTable();
        }

        drawChart();
    }

    //getters setters
    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBorders(selected);

    }

    // header tools
    private ToolButton maximizeToolbarButton() {
        return new ToolButton("x-tool-maximize",
                new SelectionListener<IconButtonEvent>() {
                    boolean isMax;

                    @Override
                    public void componentSelected(final IconButtonEvent ce) {
                        final Window window = new Window();

                        final AnnotatedTimeLinePanel panel = new AnnotatedTimeLinePanel(false, name);

                        // panel.hideHeader();
                        window.add(panel);
                        window.setWidth(800);
                        window.setHeight(800);

                        window.show();
                        panel.resize(770, 790);
                        for (final EntityName pointName : points.keySet()) {
                            panel.addPoint(points.get(pointName));
                        }

                    }
                });
    }

    private ToolButton closeToolbarButton() {
        return new ToolButton("x-tool-close",
                new SelectionListener<IconButtonEvent>() {
                    boolean isMax;

                    @Override
                    public void componentSelected(final IconButtonEvent ce) {
                        notifyChartRemovedListener(name);
                    }
                });
    }

}
