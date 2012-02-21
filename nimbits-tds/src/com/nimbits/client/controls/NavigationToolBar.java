package com.nimbits.client.controls;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.icons.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.panels.*;
import com.nimbits.client.service.entity.*;
import com.nimbits.shared.*;

import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 2/17/12
 * Time: 12:12 PM
 */
public class NavigationToolBar extends ToolBar {
    private List<EntityModifiedListener> entityModifiedListeners;
    private List<ExpandListener> expandListeners;
    EntityTree<ModelData> tree;
    Map<String, String> settings;
    public NavigationToolBar(EntityTree<ModelData> tree, Map<String, String> settings) {
        this.tree = tree;
        this.settings = settings;
        this.entityModifiedListeners = new ArrayList<EntityModifiedListener>();
        this.expandListeners = new ArrayList<ExpandListener>();
        add(addNewPointButton());
        add(new SeparatorToolItem());
        // if (! clientType.equals(ClientType.android)) {
        add(addNewFileButton());
        add(addNewCategoryButton());
        // }
        add(expandAllButton());
    }

    public void addEntityModifiedListeners(final EntityModifiedListener listener) {
        this.entityModifiedListeners.add(listener);
    }

    void notifyEntityModifiedListener(final GxtModel model, final Action action)  {
        for (EntityModifiedListener listener : entityModifiedListeners) {
            listener.onEntityModified(model, action);
        }
    }

    public interface ExpandListener {
        void onExpand() ;

    }

    public void addExpandListeners(final ExpandListener listener) {
        this.expandListeners.add(listener);
    }

    void notifyExpandListener()  {
        for (ExpandListener listener : expandListeners) {
            listener.onExpand();
        }
    }

    public interface EntityModifiedListener {
        void onEntityModified(final GxtModel model, final Action action) ;

    }

    private Button addNewPointButton() {
        final Button newPoint = new Button("");
        newPoint.setText("New Data Point");
        newPoint.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.addNew()));
        newPoint.setToolTip(Const.MESSAGE_NEW_POINT);
        newPoint.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                final MessageBox box = MessageBox.prompt(
                        Const.MESSAGE_NEW_POINT,
                        Const.MESSAGE_NEW_POINT_PROMPT);
                box.addCallback(createNewPointListener);
            }
        });
        return newPoint;
    }

    private Button expandAllButton() {
        final Button button = new Button("");
        // newPoint.setText("New Data Point");
        button.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.expand()));
        button.setToolTip("expand all");
        button.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                  notifyExpandListener();


            }
        });
        return button;
    }


    private Button addNewFileButton() {
        Button newDiagram = new Button();
        newDiagram.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.diagram()));
        newDiagram.setToolTip("Upload a file");

        newDiagram.addListener(Events.OnClick, addFileListener);
        return newDiagram;
    }

    private Button addNewCategoryButton() {
        final Button newCategory = new Button();
        newCategory.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.category()));
        newCategory.setToolTip(Const.MESSAGE_ADD_CATEGORY);
        newCategory.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(final BaseEvent be) {
                final MessageBox box = MessageBox.prompt(Const.MESSAGE_NEW_CATEGORY,
                        Const.MESSAGE_NEW_CATEGORY_PROMPT);
                box.addCallback(new Listener<MessageBoxEvent>() {
                    @Override
                    public void handleEvent(final MessageBoxEvent be) {
                        final String newEntityName = be.getValue();
                        final EntityName categoryName = CommonFactoryLocator.getInstance().createName(newEntityName);

                        final EntityServiceAsync service = GWT.create(EntityService.class);
                        Entity entity = EntityModelFactory.createEntity(categoryName, EntityType.category);

                        service.addUpdateEntity(entity,
                                new AsyncCallback<Entity>() {
                                    @Override
                                    public void onFailure(Throwable caught) {

                                        Info.display(Const.WORD_ERROR,
                                                caught.getMessage());
                                    }

                                    @Override
                                    public void onSuccess(final Entity result) {
                                        notifyEntityModifiedListener(new GxtModel(result), Action.create);

                                    }
                                });

                    }
                });
            }
        });
        return newCategory;
    }

    private final Listener<MessageBoxEvent> createNewPointListener = new Listener<MessageBoxEvent>() {
        private String newEntityName;

        @Override
        public void handleEvent(MessageBoxEvent be) {
            newEntityName = be.getValue();
            if (!Utils.isEmptyString(newEntityName)) {
                final MessageBox box = MessageBox.wait("Progress",
                        "Creating your data point channel into the cloud", "Creating: " + newEntityName);
                box.show();
                EntityServiceAsync service = GWT.create(EntityService.class);
                EntityName name = CommonFactoryLocator.getInstance().createName(newEntityName);
                //     Entity entity = EntityModelFactory.createEntity(name, EntityType.point);
                service.addUpdateEntity(name, EntityType.point,  new AsyncCallback<Entity>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Info.display("Could not create "
                                + newEntityName,
                                caught.getMessage());
                        box.close();
                    }

                    @Override
                    public void onSuccess(Entity result) {

                        notifyEntityModifiedListener(new GxtModel(result), Action.create);

                        box.close();
                    }
                });


            }
        }
    };
    private final Listener<BaseEvent> addFileListener = new Listener<BaseEvent>() {
        @Override
        public void handleEvent(final BaseEvent be) {
            final Window w = new Window();
            w.setAutoWidth(true);
            w.setHeading(Const.MESSAGE_UPLOAD_SVG);
            FileUploadPanel p = new FileUploadPanel(UploadType.newFile);
            p.addFileAddedListeners(new FileUploadPanel.FileAddedListener() {

                @Override
                public void onFileAdded()  {
                    w.hide();
                    notifyEntityModifiedListener(null, Action.refresh);

                }
            });

            w.add(p);
            w.show();
        }
    };

}
