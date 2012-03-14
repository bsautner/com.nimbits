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

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.dnd.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
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
import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.timespan.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.recordedvalues.*;

import java.util.*;

public class AnnotatedTimeLinePanel extends LayoutContainer {
    private final DateTimeFormat fmt = DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME);

    private AnnotatedTimeLine line;
    private ContentPanel mainPanel;
    private DataTable dataTable = null;
    private final Map<EntityName, Entity> points;
    private final Map<EntityName, List<Value>> valueMap;
    private final TextField endDateSelector;
    private final TextField startDateSelector;
    private final List<ChartRemovedListener> chartRemovedListeners;
    private Timespan timespan;
    private boolean headerVisible;
    private final String name;
    private boolean selected;


    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
        refreshSize(width, height);
    }

    // ChartRemoved Click Handlers
    public interface ChartRemovedListener {
        void onChartRemovedClicked();
    }

    void addChartRemovedClickedListeners(final ChartRemovedListener listener) {
        chartRemovedListeners.add(listener);
    }

    void notifyChartRemovedListener() {
        for (ChartRemovedListener ChartRemovedClickedListener : chartRemovedListeners) {
            ChartRemovedClickedListener.onChartRemovedClicked();
        }
    }

    public AnnotatedTimeLinePanel(final boolean showHeader, final String name) {
        this.headerVisible = showHeader;
        this.name = name;
        points = new HashMap<EntityName, Entity>();
        valueMap = new HashMap<EntityName, List<Value>>();
        endDateSelector = new TextField();
        startDateSelector = new TextField();
        chartRemovedListeners = new ArrayList<ChartRemovedListener>();
    }

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
    //data

    private void addPointDataToTable(final GxtModel entity, final List<Value> values) {
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
            if (valueMap.containsKey(entity.getName())) {
                valueMap.get(entity.getName()).addAll(values);
            }
            else {
                valueMap.put(entity.getName(), values);
            }
            for (final Value v : values) {

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


    public void addValue(final GxtModel model, final Value value) {
        if (points.size() == 0 || points.containsKey(model.getName()))  {
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
            addPointDataToTable(model, Arrays.asList(value));

            drawChart();
        }
    }

    private void drawChart() {
        layout();
        line.draw(dataTable, createOptions());

    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);



        setLayout(new FillLayout());
        mainPanel = new ContentPanel();
        mainPanel.setBodyBorder(false);
        mainPanel.setHeaderVisible(headerVisible);

        mainPanel.setFrame(false);
        mainPanel.setTopComponent(toolbar());
        mainPanel.setHeight("100%");

        if (headerVisible) {
            mainPanel.setHeading(name);
//            mainPanel.getHeader().addTool(
//                    maximizeToolbarButton());
            mainPanel.getHeader().addTool(
                    closeToolbarButton());
        }
        setDropTarget(mainPanel);
        add(mainPanel);
        initChart();
        layout(true);
    }


    private void initChart() {
        Runnable onLoadCallback = new Runnable() {
            @Override
            public void run() {
                if (line != null) {
                    mainPanel.remove(line);
                    line = null;
                }
                dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.DATETIME, Const.WORD_DATE);
                line = new AnnotatedTimeLine(dataTable, createOptions(), "100%", "100%");
                mainPanel.add(line);
                addEmptyDataToTable();

                layout();
                if (points != null && points.size() > 0) {

                    refreshChart();

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

    public void refreshSize(int width, int height) {
        if (width > 0 && line != null) {
            Runnable onLoadCallback = new Runnable() {
                @Override
                public void run() {
                    mainPanel.remove(line);
                    line = new AnnotatedTimeLine(dataTable, createOptions(), "100%", "100%");
                    mainPanel.add(line);
                    doLayout();
                }
            };

            VisualizationUtils.loadVisualizationApi(onLoadCallback,
                    AnnotatedTimeLine.PACKAGE);
        }
    }
    private void refreshChart()   {

        try {
            timespan = TimespanServiceClientImpl.createTimespan(startDateSelector.getValue().toString(), endDateSelector.getValue().toString());

            if (line != null && timespan != null) {
                line.setVisibleChartRange(timespan.getStart(), timespan.getEnd());
                dataTable = DataTable.create();
                dataTable.addColumn(ColumnType.DATETIME, "Date");

                for (Entity entity : points.values()) {
                    addEntityModel(new GxtModel(entity));
                }
            }
        } catch (NimbitsException e) {
            GWT.log(e.getMessage(), e);
        }

    }

    public void addEntityModel(GxtModel model) {
        //  Entity entity = model.getBaseEntity();
        if (!points.containsKey(model.getName()) && points.size() < 10) {
            if (model.getEntityType().equals(EntityType.point)) {
                points.put(model.getName(), model.getBaseEntity());
                addPointToChart(model);
            }
        }
        for (ModelData child : model.getChildren()) {
            addEntityModel((GxtModel) child);
        }
    }

    private void addPointToChart(final GxtModel model) {

        final int start = 0;
        final int end = 1000;

        if (timespan == null) {
            loadValuesThatExist(model);
        } else {
            loadMemCache(model);
            loadDataSegment(model, start, end);
        }
    }

    private void loadValuesThatExist(final GxtModel model) {
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);

        dataService.getTopDataSeries(model.getBaseEntity(), 100, new Date(), new AsyncCallback<List<Value>>() {
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


                addPointDataToTable(model, result);

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

    private void loadDataSegment(final GxtModel p, final int start, final int end) {
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        final MessageBox box = MessageBox.wait("Progress",
                "Loading " + p.getName().getValue() + " archived values " + start + " to " + end, "Loading...");
        box.show();
        //   Timespan timespan = new TimespanModel(startDate, endDate);
        dataService.getPieceOfDataSegment(p.getBaseEntity(), timespan, start, end, new AsyncCallback<List<Value>>() {
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

    private void loadMemCache(final GxtModel model) {
        final RecordedValueServiceAsync dataService = GWT.create(RecordedValueService.class);
        final MessageBox box = MessageBox.wait("Progress",
                "Loading Buffered Data", "Loading...");
        box.show();
        //   Timespan timespan = new TimespanModel(startDate, endDate);
        dataService.getCache(model.getBaseEntity(), new AsyncCallback<List<Value>>() {
            @Override
            public void onFailure(final Throwable caught) {
                box.close();
            }

            @Override
            public void onSuccess(final List<Value> result) {
                addPointDataToTable(model, result);

                box.close();
            }
        });


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
                    handleDrop(p);
                }
            }
        };
    }

    private void handleDrop(final GxtModel p) {
        if (p.getEntityType().equals(EntityType.point)) {
            addEntityModel(p);
        }
        for (final ModelData x : p.getChildren()) {
            handleDrop((GxtModel)x);
        }
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

    public void removePoint(final Entity entity) {
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

    private ToolButton closeToolbarButton() {
        return new ToolButton("x-tool-close",
                new SelectionListener<IconButtonEvent>() {
                    boolean isMax;

                    @Override
                    public void componentSelected(final IconButtonEvent ce) {
                        notifyChartRemovedListener();
                    }
                });
    }
    private ToolBar toolbar() {
        final ToolBar toolBar = new ToolBar();
        final Button export = exportButton();
        final Button resetChartButton = resetChartButton();
        final Button refresh = refreshButton();


        final Button startDateMenu = new Button();
        startDateMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.calendar()));
        initStartDateSelector();
        initEndDateSelector();

        toolBar.add(startDateSelector);

        toolBar.add(endDateSelector);

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


        Button refreshRange = refreshRangeButton(min, max);
        toolBar.add(refreshRange);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(resetChartButton);

        toolBar.add(export);
        return toolBar;
    }

    private Button resetChartButton() {
        Button button = new Button();
        button.setToolTip("Reset Chart");
        button.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.delete()));
        button.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                for (Entity entity : points.values()) {
                    removePoint(entity);
                }

            }
        });
        return button;
    }

    private Button refreshRangeButton(final NumberField min, final NumberField max) {
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
        return refreshRange;
    }

    private void initEndDateSelector() {
        if (timespan != null) {
            endDateSelector.setValue(fmt.format(timespan.getEnd()));
        }
        endDateSelector.setSelectOnFocus(false);
        endDateSelector.setToolTip("End Date");
        endDateSelector.addListener(Events.KeyPress, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == 13) {

                    refreshChart();

                }
            }
        });
    }

    private void initStartDateSelector() {
        startDateSelector.setSelectOnFocus(false);
        if (timespan != null) {
            startDateSelector.setValue(fmt.format(timespan.getStart()));
        }
        startDateSelector.setToolTip("Start Date");

        startDateSelector.addListener(Events.KeyPress, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == 13) {

                    refreshChart();

                }
            }
        });
    }

    private Button refreshButton() {
        final Button refresh = new Button();
        refresh.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh2()));
        refresh.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent be) {

                refreshChart();

            }
        });
        return refresh;
    }

    private Button exportButton() {
        final Button export = new Button();
        export.setText("Export and Report");
        export.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.table()));
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
        return export;
    }
}
