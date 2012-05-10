package com.nimbits.client.ui.controls;

import com.extjs.gxt.ui.client.widget.form.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/20/12
 * Time: 1:10 PM
 */
public class EntityPanel extends FormPanel {

    private final ProtectionLevelOptions protectionOptions;


    public EntityPanel(Entity entity) {

        setEncoding(FormPanel.Encoding.MULTIPART);
        setHeaderVisible(false);
        setFrame(false);

        final TextArea description = new TextArea();
        description.setFieldLabel("Description");
        description.setName(Parameters.description.getText());
        add(description);

        protectionOptions = new ProtectionLevelOptions(entity);

        add(protectionOptions);
       // protectionOptions.addHandler(Events.OnClick, ClickEvent)
        final HiddenField<ProtectionLevel> protectionLevelHiddenField = new HiddenField<ProtectionLevel>();
        protectionLevelHiddenField.setName(Parameters.protection.getText());
        add(protectionLevelHiddenField);
        protectionLevelHiddenField.setValue(protectionOptions.getProtectionLevel());
    }


    public ProtectionLevel getProtectionLevel() {
        return protectionOptions.getProtectionLevel();
    }


}
