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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.user.User;
import com.nimbits.client.service.settings.SettingsServiceRpc;
import com.nimbits.client.service.settings.SettingsServiceRpcAsync;
import com.nimbits.client.service.value.ValueServiceRpc;
import com.nimbits.client.service.value.ValueServiceRpcAsync;
import com.nimbits.client.ui.helper.FeedbackHelper;

public class ChartPanel extends LayoutContainer {


    private final VerticalPanel vp;


    SettingsServiceRpcAsync settingsService;
    private Entity entity;
    private User user;
    private final ValueServiceRpcAsync valueService;
    private final Dialog progressBar;

    public ChartPanel(User user, Entity entity) {

        this.user = user;
        this.entity = entity;
        settingsService = GWT.create(SettingsServiceRpc.class);
        valueService = GWT.create(ValueServiceRpc.class);
        this.progressBar = createProgressBar("Loading...");

        vp = new VerticalPanel();
        vp.setSpacing(5);


    }

    private Dialog createProgressBar(final String title) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.auto();


        Dialog dialog = new Dialog();


        dialog.add(progressBar);
        dialog.setButtons("");
        dialog.setAutoHeight(true);
        dialog.setClosable(false);
        dialog.addText(title);
        dialog.setResizable(false);


        return dialog;
    }

    public static native DataTable toDataTable(String json) /*-{
        return new $wnd.google.visualization.DataTable(eval("(" + json + ")"));
    }-*/;

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);


        Runnable onLoadCallback = new Runnable() {
            public void run() {

//                progressBar.toFront();
//                progressBar.show();
//                progressBar.toFront();
                valueService.getChartTable(user, entity, null, new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        FeedbackHelper.showError(throwable);
                        progressBar.hide();
                    }

                    @Override
                    public void onSuccess(String dataTable) {

                        DataTable data = toDataTable(dataTable);
                        AnnotatedTimeLine.Options options = AnnotatedTimeLine.Options.create();
                        options.setDisplayAnnotations(true);
                        options.setDisplayZoomButtons(true);
                        options.setScaleType(AnnotatedTimeLine.ScaleType.ALLFIXED);
                        options.setLegendPosition(AnnotatedTimeLine.AnnotatedLegendPosition.SAME_ROW);
                        AnnotatedTimeLine chart = new AnnotatedTimeLine(data, options, "750px", "450px");


                        vp.add(chart);

                        add(vp);
                        progressBar.hide();
                        doLayout();
                    }
                });

            }
        };

        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback, AnnotatedTimeLine.PACKAGE);


    }


}
