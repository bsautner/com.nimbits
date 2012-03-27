package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.store.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.feed.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.service.feed.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.icons.*;

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
    private ComboBox<FeedTypeOption> feedType;

    private String feedOwnersUUID;
    private final User user;

    public FeedPanel(final User user) {

        this.user = user;
        feedOwnersUUID = user.getUuid();
    }

    @Override
    protected void onAttach() {
        Timer updater = new Timer() {
            @Override
            public void run() {

                updateValues(false);

            }
        };
        updater.scheduleRepeating(Const.DEFAULT_TIMER_UPDATE_SPEED);
        updater.run();
        super.onAttach();
    }

    private ComboBox<FeedTypeOption> optionComboBox(final FeedType selectedValue) {
        ComboBox<FeedTypeOption> combo = new ComboBox<FeedTypeOption>();

        ArrayList<FeedTypeOption> ops = new ArrayList<FeedTypeOption>();

        ops.add(new FeedTypeOption(FeedType.all));
        ops.add(new FeedTypeOption(FeedType.info));
        ops.add(new FeedTypeOption(FeedType.data));
        ops.add(new FeedTypeOption(FeedType.error));
        ops.add(new FeedTypeOption(FeedType.system));

        ListStore<FeedTypeOption> store = new ListStore<FeedTypeOption>();

        store.add(ops);

        //  combo.setFieldLabel(title);
        combo.setDisplayField(Params.PARAM_NAME);
        combo.setValueField(Params.PARAM_VALUE);
        combo.setTriggerAction(ComboBox.TriggerAction.ALL);
        combo.setStore(store);

        FeedTypeOption selected = combo.getStore().findModel(Params.PARAM_VALUE, selectedValue.getCode());
        combo.setValue(selected);

        return combo;

    }

    private void updateValues(boolean reset) {
        final ListStore<GxtFeedModel> store  = view.getStore();
        if (reset) {
            store.removeAll();
        }
        if (store != null) {
            FeedAsync service = GWT.create(Feed.class);
            service.getFeed(10, feedOwnersUUID, new AsyncCallback<List<FeedValue>>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(final List<FeedValue> result) {

                    GxtFeedModel model;
                    for (final FeedValue v : result) {

                        model = new GxtFeedModel(v);
                        FeedType type = feedType.getValue().type;
                        if (type.equals(FeedType.all) || type.equals(v.getFeedType())) {
                            if (store.findModel(Params.PARAM_HTML, model.getHtml()) == null) {
                                store.insert(model, 0);
                           }
                        }
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
                model.set(Params.PARAM_PATH, GWT.getHostPageBaseURL() + model.get(Params.PARAM_PATH));
                return model;
            }

        };


        FeedAsync service = GWT.create(Feed.class);
        final int FEED_COUNT = 30;
        service.getFeed(FEED_COUNT, feedOwnersUUID, new AsyncCallback<List<FeedValue>>() {
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

        ToolBar bar = new ToolBar();

        ButtonGroup group = new ButtonGroup(1);
        group.setHeading("Feed Options");
        group.setHeaderVisible(false);
        group.setBodyBorder(false);
        group.setAutoWidth(true);
        Button btn = new Button("Refresh");
        btn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refresh()));
        btn.setIconAlign(Style.IconAlign.LEFT);

        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent buttonEvent) {
                 feedOwnersUUID = user.getUuid();
            }
        });


        feedType = optionComboBox(FeedType.all);
        feedType.addSelectionChangedListener(new SelectionChangedListener<FeedTypeOption>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<FeedTypeOption> feedTypeOptionSelectionChangedEvent) {

                updateValues(true);
            }
        });
        group.add(new LabelToolItem("Show:"));
        group.add(feedType);
        group.setBodyBorder(true);

        group.add(new LabelToolItem("Switch to connected user's Feed:"));
        EntityCombo entityCombo = new EntityCombo(EntityType.userConnection, "", "");
        entityCombo.addSelectionChangedListener(new SelectionChangedListener<GxtModel>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<GxtModel> gxtModelSelectionChangedEvent) {
               feedOwnersUUID =gxtModelSelectionChangedEvent.getSelectedItem().getUUID();
                updateValues(true);
            }
        });
        group.add(entityCombo);
        group.add(btn);
        bar.add(group);

        ContentPanel main = new ContentPanel();
        main.setBorders(false);
        main.setBodyBorder(false);
        main.setLayout(new FillLayout());
        main.setHeaderVisible(false);
        main.setTopComponent(bar);
        main.setScrollMode(Style.Scroll.AUTOY);
        main.add(view);
        main.setHeight(800);

        add(main);


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
        updateValues(true);

    }

    private class FeedTypeOption extends BaseModelData {
        FeedType type;


        public FeedTypeOption(FeedType value) {
            this.type = value;
            set(Params.PARAM_VALUE, value.getCode());
            set(Params.PARAM_NAME, value.getText());
        }

        public FeedType getMethod() {
            return type;
        }
    }


//    private class UserOption extends BaseModelData {
//        User user;
//
//
//        public UserOption(User value) {
//            this.user = value;
//            set(Const.PARAM_VALUE,value.getUuid());
//            set(Const.Params.PARAM_NAME, value.getEmail());
//        }
//
//        public User getUser() {
//            return user;
//        }
//    }

}

