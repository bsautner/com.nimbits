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
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treegrid.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;

import java.util.*;

public class ColumnConfigs {



    public ColumnConfig timestampColumn() {
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

        return columnTime;
    }

    public ColumnConfig noteColumn( ) {
        final ColumnConfig columnNote = new ColumnConfig();
        columnNote.setId(Const.PARAM_NOTE);
        columnNote.setHeader(Const.WORD_ANNOTATION);
        columnNote.setWidth(250);

        final TextField<String> noteText = new TextField<String>();
        noteText.setAllowBlank(true);
        columnNote.setEditor(new CellEditor(noteText));
        columnNote.setAlignment(Style.HorizontalAlignment.LEFT);
        return (columnNote);
    }

    public ColumnConfig addDataColumn( ) {
        final ColumnConfig columnData = new ColumnConfig();
        columnData.setId(Const.PARAM_DATA);
        columnData.setHeader(Const.WORD_DATA);
        columnData.setWidth(250);

        final TextField<String> dataText = new TextField<String>();
        dataText.setAllowBlank(true);
        columnData.setEditor(new CellEditor(dataText));
        columnData.setAlignment(Style.HorizontalAlignment.LEFT);
        return (columnData);
    }

    public ColumnConfig currentValueColumn() {
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

        return (columnValue);
    }

    public ColumnConfig pointNameColumn(boolean useRenderer) {
        final ColumnConfig nameColumn =  new ColumnConfig(Const.PARAM_NAME, Const.MESSAGE_DATA_POINT, 150);
        nameColumn.setId(Const.PARAM_NAME);
        nameColumn.setHeader(Const.MESSAGE_DATA_POINT);
        nameColumn.setAlignment(Style.HorizontalAlignment.LEFT);
        nameColumn.setWidth(150);
        if (useRenderer) {
        nameColumn.setRenderer(new TreeGridCellRenderer<ModelData>());
        }
        TextField<String> nameText = new TextField<String>();
        nameText.setAllowBlank(false);
        nameText.setSelectOnFocus(true);
        return nameColumn;

    }


    public ColumnConfig alertColumn(final Map<String, Entity> points) {

        final GridCellRenderer<GxtModel> propertyButtonRenderer = new GridCellRenderer<GxtModel>() {

            public Object render(final GxtModel model, final String property, final ColumnData config, final int rowIndex,
                                 final int colIndex, final ListStore<GxtModel> store, final Grid<GxtModel> grid) {

                final Button b = new Button((String) model.get(property), new SelectionListener<ButtonEvent>() {
                    @Override
                    public void componentSelected(final ButtonEvent ce) {
                        final Entity p = points.get(model.getUUID());
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
                switch (model.getAlertType()) {
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

        final ColumnConfig c = new ColumnConfig();
        c.setId(Const.PARAM_STATE);
        c.setHeader("state");
        c.setWidth(35);
        c.setAlignment(Style.HorizontalAlignment.LEFT);
        c.setRenderer(propertyButtonRenderer);
        return (c);
    }


}