package com.nimbits.client.model.setting;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/5/11
 * Time: 3:41 PM
 */
public interface Setting extends Serializable {
    String getName();

    String getValue();
}
