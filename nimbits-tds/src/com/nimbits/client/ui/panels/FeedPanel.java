package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.service.feed.*;

import java.util.*;


/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/24/12
 * Time: 2:27 PM
 */
public class FeedPanel  extends LayoutContainer {
    ListView<GxtFeedModel> view;
    //ContentPanel panel;

    @Override
    protected void onAttach() {
        Timer updater = new Timer() {
            @Override
            public void run() {

                updateValues();

            }
        };
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    private void updateValues() {
        final ListStore<GxtFeedModel> store  = view.getStore();
        if (store != null) {
            FeedAsync service = GWT.create(Feed.class);
            service.getFeed(10, new AsyncCallback<List<FeedValue>>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(final List<FeedValue> result) {

                    GxtFeedModel model;
                    for (final FeedValue v : result) {
                        model = new GxtFeedModel(v);
                        if (store.findModel(Const.PARAM_HTML, model.getHtml()) == null) {
                            store.insert(model, 0);
                            // store.add(model);
                        }
                    }
                    if (store.getModels() != null && store.getModels().size() > 8) {
                        setScrollMode(Style.Scroll.ALWAYS);
                    }
                    layout(true);
                }
            });
        }
    }



    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setBorders(false);

        view = new ListView<GxtFeedModel>() {
            @Override
            protected GxtFeedModel prepareData(GxtFeedModel model) {
               // String s = model.get(Const.Params.PARAM_NAME);
                //  model.set("shortName", Format.ellipse(s, 15));
                model.set(Const.Params.PARAM_PATH, GWT.getHostPageBaseURL() + model.get(Const.Params.PARAM_PATH));
                return model;
            }

        };


        FeedAsync service = GWT.create(Feed.class);
        final int FEED_COUNT = 30;
        service.getFeed(FEED_COUNT, new AsyncCallback<List<FeedValue>>() {
            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(List<FeedValue> result) {
                final ListStore<GxtFeedModel> store = new ListStore<GxtFeedModel>();

                for (final FeedValue v : result) {
                    store.add(new GxtFeedModel(v));
                }
                view.setStore(store);
                layout(true);
            }
        });

        view.setTemplate(getTemplate());
        view.setBorders(false);
        view.setItemSelector("div.thumb-wrap");
        view.getSelectionModel().addListener(Events.SelectionChange,
                new Listener<SelectionChangedEvent<BeanModel>>() {

                    public void handleEvent(SelectionChangedEvent<BeanModel> be) {
//                        panel.setHeading("Simple ListView (" + be.getSelection().size()
//                                + " items selected)");
                    }

                });

        add(view);


    }

    private native String getTemplate() /*-{
        return ['<tpl for=".">',
            '<table border = 0>',
            '<tr><td>{html}</td></tr>',
            '</table>',
            '<hr />',
            '</tpl>',
            '<div class="x-clear"></div>'].join("");

    }-*/;

    public void reload() {
        view.getStore().removeAll();
        updateValues();
    }
}

