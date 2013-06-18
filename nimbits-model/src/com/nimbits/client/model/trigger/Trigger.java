package com.nimbits.client.model.trigger;

import com.nimbits.client.model.entity.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 12:22 PM
 */
public interface Trigger extends Entity, Serializable {
    String getTarget();

    String getTrigger();

    boolean isEnabled();

    void setEnabled(boolean enabled);


    void setTarget(String target);

    void setTrigger(String trigger);
}
