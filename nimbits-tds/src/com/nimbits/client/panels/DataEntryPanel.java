package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.client.service.recordedvalues.RecordedValueService;
import com.nimbits.client.service.recordedvalues.RecordedValueServiceAsync;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/2/12
 * Time: 5:13 PM
 */
public class DataEntryPanel extends NavigationEventProvider {

    private Point point;
    VerticalPanel vp;
    private FormData formData;

    protected void onRender(final Element target, final int index) {

        super.onRender(target, index);
        formData = new FormData("-0");
        vp = new VerticalPanel();
        vp.setSpacing(0);

        createForm1();
        add(vp);


    }

    public DataEntryPanel(Point point) {
        this.point = point;
    }

    private void createForm1() {
        FormPanel simple = new FormPanel();
        simple.setHeaderVisible(false);
        simple.setFrame(false);
        simple.setBodyBorder(false);
        simple.setWidth(350);

        final NumberField value = new NumberField();
        value.setFieldLabel("Value");
        value.setAllowBlank(false);
        value.setWidth(50);
        simple.add(value, formData);


        final TextArea note = new TextArea();
        note.setPreventScrollbars(true);
        note.setFieldLabel("Annotation");
        simple.add(note, formData);
        note.setWidth(100);
        Button b = new Button("Submit");
        simple.addButton(b);


        b.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                RecordedValueServiceAsync service = GWT.create(RecordedValueService.class);
                Value v = ValueModelFactory.createValueModel(value.getValue().doubleValue(), note.getValue());
                try {
                    service.recordValue(point, v, new AsyncCallback<Value>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        @Override
                        public void onSuccess(Value value) {
                            notifyValueEnteredListener(point, value);
                        }
                    });
                } catch (NimbitsException e) {

                }

                //
            }
        });


        simple.setButtonAlign(Style.HorizontalAlignment.CENTER);

        FormButtonBinding binding = new FormButtonBinding(simple);
        binding.addButton(b);


        vp.add(simple);
    }
}
