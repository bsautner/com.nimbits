/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.chart;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartDTO implements Serializable {

    @Expose
    private List<ChartColumnDefinition> cols = new ArrayList<ChartColumnDefinition>();
    @Expose
    private List<Row> rows = new ArrayList<Row>();
    @Expose
    private Object p;

    /**
     * @return The cols
     */
    public List<ChartColumnDefinition> getCols() {
        return cols;
    }

    /**
     * @param cols The cols
     */
    public void setCols(List<ChartColumnDefinition> cols) {
        this.cols = cols;
    }

    /**
     * @return The rows
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * @param rows The rows
     */
    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    /**
     * @return The p
     */
    public Object getP() {
        return p;
    }

    /**
     * @param p The p
     */
    public void setP(Object p) {
        this.p = p;
    }

}
