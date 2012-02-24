package com.nimbits.client.panels;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.nimbits.client.model.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:27 PM
 */
public class FeedPanel  extends LayoutContainer {
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        final ContentPanel panel = new ContentPanel( );
        panel.setCollapsible(false);
        panel.setAnimCollapse(false);
        panel.setFrame(true);
        panel.setId("images-view");
        panel.setHeaderVisible(true);
        panel.setHeading("Data Feed");
        panel.setWidth(175);
       // panel.setHeight("100%");
      //  panel.setAutoHeight(true);
        panel.setBodyBorder(false);
        ListStore<GxtValueModel> store = new ListStore<GxtValueModel>();

        store.add(new GxtValueModel());

        ListView<GxtValueModel> view = new ListView<GxtValueModel>() {
            @Override
            protected GxtValueModel prepareData(GxtValueModel model) {
                String s = model.get("name");
                //  model.set("shortName", Format.ellipse(s, 15));
                model.set("path", GWT.getHostPageBaseURL() + model.get("path"));
                return model;
            }

        };

        view.setTemplate(getTemplate());
        view.setStore(store);
        view.setHeight("100%");
        view.setItemSelector("div.thumb-wrap");
        view.getSelectionModel().addListener(Events.SelectionChange,
                new Listener<SelectionChangedEvent<BeanModel>>() {

                    public void handleEvent(SelectionChangedEvent<BeanModel> be) {
                        panel.setHeading("Simple ListView (" + be.getSelection().size()
                                + " items selected)");
                    }

                });
        panel.add(view);
        add(panel);


    }

    private native String getTemplate() /*-{
        return ['<tpl for=".">',
            '<p>{name}</p>',
            '</tpl>',
            '<div class="x-clear"></div>'].join("");

    }-*/;
}

