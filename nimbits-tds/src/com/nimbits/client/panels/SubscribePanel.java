package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import static com.google.gwt.user.client.Window.*;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.subscription.*;
import com.nimbits.client.service.datapoints.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 1/17/12
 * Time: 3:29 PM
 */
public class SubscribePanel extends LayoutContainer {

    VerticalPanel vp;
    private String pointUUID;
    private Point point;

    public SubscribePanel(String pointUUID) {
      this.pointUUID = pointUUID;
    }

    @Override
    protected void onRender(final Element parent, final int index) {
        super.onRender(parent, index);
        setLayout(new FillLayout());
        FormData formdata = new FormData("-20");
        vp = new VerticalPanel();
        vp.setBorders(false);
        PointServiceAsync service = GWT.create(PointService.class);
        service.getPointByUUID(pointUUID, new AsyncCallback<Point>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log(caught.getMessage());
            }

            @Override
            public void onSuccess(Point result) {
                createForm();
                add(vp);
            }
        });


    }

    private void createForm() {

        final FormPanel panel = new FormPanel();
        final RadioGroup option = new RadioGroup();
        final Radio csvSeparateColumns = new Radio();

        option.setFieldLabel("Report Type");
        option.setOrientation(Style.Orientation.VERTICAL);

        //  descriptiveStatistics.setBoxLabel("Descriptive Statistics (Beta)");
        // descriptiveStatistics.setValue(true);
        final Radio dataView = new Radio();
        dataView.setBoxLabel("Current Status Report");
        dataView.setValue(true);

        csvSeparateColumns.setValue(true);
        csvSeparateColumns.setBoxLabel("Export to Spreadsheet (CSV with separate columns)");
        // possibleContinuation.setBoxLabel("Calculate a Possible Continuation (beta)");
        panel.setLayout(new FitLayout());
        panel.setFrame(false);
        panel.setHeaderVisible(false);
        panel.setBodyBorder(false);
        //  panel.setWidth(480);
        // panel.setHeight(360);

        panel.addListener(Events.Submit, new Listener<FormEvent>() {

            @Override
            public void handleEvent(FormEvent be) {
                alert(be.getResultHtml());
            }
        });

        Button submit = new Button("Submit");

        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                PointServiceAsync pointService = GWT.create(PointService.class);
                final MessageBox box = MessageBox.wait("Progress",
                        "Subscribing to your point", "loading...");
                box.show();
                final ExportType exportType;

                if (csvSeparateColumns.getValue()) {
                    exportType = ExportType.csvSeparateColumns;
                } else if (dataView.getValue()) {
                    exportType = ExportType.currentStatusReport;

                    //  } else if (possibleContinuation.getValue()) {
                    //      exportType = ExportType.possibleContinuation;
                } else {
                    exportType = ExportType.csvSeparateColumns;
                }
                SubscriptionDeliveryMethod dataUpdateAlertMethod = SubscriptionDeliveryMethod.none;
                SubscriptionDeliveryMethod alarmStateChangeMethod = SubscriptionDeliveryMethod.none;
                SubscriptionDeliveryMethod propertyChangeMethod = SubscriptionDeliveryMethod.none;
                Subscription subscription = SubscriptionFactory.createSubscription(dataUpdateAlertMethod,
                        alarmStateChangeMethod, propertyChangeMethod);

                pointService.subscribe(point, subscription , new AsyncCallback<Subscription>() {
                    @Override
                    public void onFailure(Throwable e) {
                        GWT.log(e.getMessage(), e);
                        box.close();
                        MessageBox.alert("Error", e.getMessage(), null);
                    }

                    @Override
                    public void onSuccess(final Subscription result) {

                    }
                });

            }
        });


       // Html h = new Html(s);

     //   vp.add(h);
        option.add(csvSeparateColumns);
        //  option.add(descriptiveStatistics);
        //  option.add(possibleContinuation);
        panel.add(option);
        panel.add(submit);
        vp.add(panel);
    }
}
