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

package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.treegrid.*;
import com.google.gwt.i18n.client.*;
import com.nimbits.client.model.*;

public class ColumnConfigs {



    public static ColumnConfig timestampColumn() {
        final DateField dateField = new DateField();
        dateField.getPropertyEditor().setFormat(
                DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME));

        final ColumnConfig columnTime = new ColumnConfig();
        columnTime.setId(Const.Params.PARAM_TIMESTAMP);
        columnTime.setHeader(Const.WORD_TIMESTAMP);
        columnTime.setAlignment(Style.HorizontalAlignment.LEFT);
        columnTime.setWidth(175);
        columnTime.setDateTimeFormat(DateTimeFormat
                .getFormat(Const.FORMAT_DATE_TIME));
        columnTime.setEditor(new CellEditor(dateField));

        return columnTime;
    }

    public static ColumnConfig noteColumn( ) {
        final ColumnConfig columnNote = new ColumnConfig();
        columnNote.setId(Const.Params.PARAM_NOTE);
        columnNote.setHeader(Const.WORD_ANNOTATION);
        columnNote.setWidth(250);

        final TextField<String> noteText = new TextField<String>();
        noteText.setAllowBlank(true);
        columnNote.setEditor(new CellEditor(noteText));
        columnNote.setAlignment(Style.HorizontalAlignment.LEFT);
        return (columnNote);
    }

    public static ColumnConfig dataColumn() {
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

    public static ColumnConfig currentValueColumn() {
        final NumberField n = new NumberField();
        n.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());
        n.setSelectOnFocus(true);
        n.setEditable(true);

        final ColumnConfig columnValue = new ColumnConfig();
        columnValue.setId(Const.PARAM_VALUE);
        columnValue.setHeader(Const.WORD_VALUE);
        columnValue.setAlignment(Style.HorizontalAlignment.CENTER);
        columnValue.setWidth(100);
        columnValue.setNumberFormat(NumberFormat.getDecimalFormat());
        CellEditor ce = new CellEditor(n);
        columnValue.setEditor(ce);

        return (columnValue);
    }

    public static ColumnConfig pointNameColumn() {
        final ColumnConfig nameColumn =  new ColumnConfig(Const.Params.PARAM_NAME, Const.MESSAGE_DATA_POINT, 150);
        nameColumn.setId(Const.Params.PARAM_NAME);
        nameColumn.setHeader("Objects");
        nameColumn.setAlignment(Style.HorizontalAlignment.LEFT);
        nameColumn.setWidth(225);
        nameColumn.setRenderer(new TreeGridCellRenderer<ModelData>());

        TextField<String> nameText = new TextField<String>();
        nameText.setAllowBlank(false);
        nameText.setSelectOnFocus(true);
        return nameColumn;

    }





}