/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.Words;
import com.nimbits.client.enums.Parameters;

public class ColumnConfigs {


    private static final String ENTITIES = "Entities";

    public static ColumnConfig timestampColumn() {
        final DateField dateField = new DateField();
        dateField.getPropertyEditor().setFormat(
                DateTimeFormat.getFormat(Const.FORMAT_DATE_TIME));

        final ColumnConfig columnTime = new ColumnConfig();
        columnTime.setId(Parameters.timestamp.getText());
        columnTime.setHeader(Words.WORD_TIMESTAMP);
        columnTime.setAlignment(Style.HorizontalAlignment.LEFT);
        columnTime.setWidth(175);
        columnTime.setDateTimeFormat(DateTimeFormat
                .getFormat(Const.FORMAT_DATE_TIME));
        columnTime.setEditor(new CellEditor(dateField));

        return columnTime;
    }

    public static ColumnConfig noteColumn( ) {
        final ColumnConfig columnNote = new ColumnConfig();
        columnNote.setId(Parameters.value.getText());
        columnNote.setHeader(Words.WORD_ANNOTATION);
        columnNote.setWidth(400);



        final TextField<String> noteText = new TextField<String>();
        noteText.setAllowBlank(true);

        columnNote.setEditor(new CellEditor(noteText));
        columnNote.setAlignment(Style.HorizontalAlignment.LEFT);
        return (columnNote);
    }

    public static ColumnConfig dataColumn() {
        final ColumnConfig columnData = new ColumnConfig();
        columnData.setId(Parameters.data.getText());
        columnData.setHeader(Words.WORD_DATA);
         columnData.setWidth(800);

        final TextField<String> dataText = new TextField<String>();
        dataText.setAllowBlank(true);
        dataText.setReadOnly(true);
        columnData.setEditor(new CellEditor(dataText));
        columnData.setAlignment(Style.HorizontalAlignment.LEFT);
        return (columnData);
    }

//    public static ColumnConfig currentValueColumn() {
//        final NumberField n = new NumberField();
//        n.getPropertyEditor().setFormat(NumberFormat.getDecimalFormat());
//        n.setSelectOnFocus(true);
//        n.setEditable(true);
//
//        final ColumnConfig columnValue = new ColumnConfig();
//        columnValue.setId(Const.PARAM_VALUE);
//        columnValue.setHeader(Const.WORD_VALUE);
//        columnValue.setAlignment(Style.HorizontalAlignment.CENTER);
//        columnValue.setWidth(100);
//        columnValue.setNumberFormat(NumberFormat.getDecimalFormat());
//        CellEditor ce = new CellEditor(n);
//        columnValue.setEditor(ce);
//
//        return (columnValue);
//    }

    public static ColumnConfig pointNameColumn() {
        final ColumnConfig nameColumn =  new ColumnConfig(Parameters.name.getText(), ENTITIES, 150);
        nameColumn.setId(Parameters.name.getText());
        nameColumn.setHeader(ENTITIES);
        nameColumn.setAlignment(Style.HorizontalAlignment.LEFT);
        nameColumn.setWidth(225);
        nameColumn.setRenderer(new TreeGridCellRenderer<ModelData>());

        TextField<String> nameText = new TextField<String>();
        nameText.setAllowBlank(false);
        nameText.setSelectOnFocus(true);
        return nameColumn;

    }





}