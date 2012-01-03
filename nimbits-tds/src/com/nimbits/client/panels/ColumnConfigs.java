package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.nimbits.client.icons.Icons;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.GxtPointModel;
import com.nimbits.client.model.point.Point;

import java.util.List;

public class ColumnConfigs {
    private final PointGridPanel pointGridPanel;

    public ColumnConfigs(PointGridPanel pointGridPanel) {
        this.pointGridPanel = pointGridPanel;
    }

    public void addTimestampColumn(final List<ColumnConfig> configs) {
        final DateField dateField = new DateField();
        dateField.getPropertyEditor().setFormat(
                DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME));

        final ColumnConfig columnTime = new ColumnConfig();
        columnTime.setId(Const.PARAM_TIMESTAMP);
        columnTime.setHeader(Const.WORD_TIMESTAMP);
        columnTime.setAlignment(Style.HorizontalAlignment.LEFT);
        columnTime.setWidth(200);
        columnTime.setDateTimeFormat(DateTimeFormat
                .getFormat(Const.FORMAT_DATE_TIME));
        columnTime.setEditor(new CellEditor(dateField));
        // column.setDateTimeFormat(DateTimeFormat.getShortDateTimeFormat());
        configs.add(columnTime);
    }

    public void addNoteColumn(final List<ColumnConfig> configs) {
        final ColumnConfig columnNote = new ColumnConfig();
        columnNote.setId(Const.PARAM_NOTE);
        columnNote.setHeader(Const.WORD_ANNOTATION);
        columnNote.setWidth(250);

        final TextField<String> noteText = new TextField<String>();
        noteText.setAllowBlank(true);
        columnNote.setEditor(new CellEditor(noteText));
        columnNote.setAlignment(Style.HorizontalAlignment.LEFT);
        configs.add(columnNote);
    }

    public void addDataColumn(final List<ColumnConfig> configs) {
        final ColumnConfig columnData = new ColumnConfig();
        columnData.setId(Const.PARAM_DATA);
        columnData.setHeader(Const.WORD_DATA);
        columnData.setWidth(250);

        final TextField<String> dataText = new TextField<String>();
        dataText.setAllowBlank(true);
        columnData.setEditor(new CellEditor(dataText));
        columnData.setAlignment(Style.HorizontalAlignment.LEFT);
        configs.add(columnData);
    }

    public void addCurrentValueColumn(final List<ColumnConfig> configs) {
        final NumberField n = new NumberField();
        n.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());
        n.setSelectOnFocus(true);
        n.setEditable(true);

        final ColumnConfig columnValue = new ColumnConfig();
        columnValue.setId(Const.PARAM_VALUE);
        columnValue.setHeader(Const.WORD_VALUE);
        columnValue.setAlignment(Style.HorizontalAlignment.CENTER);
        columnValue.setWidth(50);
        columnValue.setNumberFormat(NumberFormat.getDecimalFormat());
        CellEditor ce = new CellEditor(n);
        columnValue.setEditor(ce);

        configs.add(columnValue);
    }

    public void addPointNameColumn(final List<ColumnConfig> configs) {
        final ColumnConfig nameColumn = new ColumnConfig();
        nameColumn.setId(Const.PARAM_NAME);
        nameColumn.setHeader(Const.MESSAGE_DATA_POINT);
        nameColumn.setAlignment(Style.HorizontalAlignment.LEFT);

        nameColumn.setWidth(250);

        TextField<String> nameText = new TextField<String>();
        nameText.setAllowBlank(false);
        nameText.setSelectOnFocus(true);
        configs.add(nameColumn);
    }


    public void addAlertColumn(final List<ColumnConfig> configs) {
        final GridCellRenderer<GxtPointModel> propertyButtonRenderer = new GridCellRenderer<GxtPointModel>() {

            public Object render(final GxtPointModel model, final String property, final ColumnData config, final int rowIndex,
                                 final int colIndex, final ListStore<GxtPointModel> store, final Grid<GxtPointModel> grid) {

                final Button b = new Button((String) model.get(property), new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(final ButtonEvent ce) {
                        final Point p = pointGridPanel.getPoints().get(model.getName());
                        String u = Window.Location.getHref()
                                + "?uuid=" + p.getUUID()
                                + "&count=10";
                        Window.open(u, p.getName().getValue(), Const.PARAM_DEFAULT_WINDOW_OPTIONS);

                    }
                });

                b.setWidth(22);
                b.setToolTip(Const.MESSAGE_CLICK_TO_TREND);
                b.setEnabled(!model.isReadOnly());

                b.setBorders(false);
                switch (model.getAlertState()) {
                    case IdleAlert:
                        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.point_idle()));
                        break;
                    case HighAlert:
                        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.point_high()));
                        break;
                    case LowAlert:
                        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.point_low()));
                        break;
                    default:
                        b.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.point_ok()));

                }

                return b;
            }
        };


        final ColumnConfig propertyColumn = new ColumnConfig();
        propertyColumn.setId(Const.PARAM_STATE);
        propertyColumn.setHeader("state");
        propertyColumn.setWidth(35);
        propertyColumn.setAlignment(Style.HorizontalAlignment.LEFT);
        propertyColumn.setRenderer(propertyButtonRenderer);
        configs.add(propertyColumn);
    }


}