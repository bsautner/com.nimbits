package com.nimbits.client.model.accesskey;

import com.nimbits.client.model.entity.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:21 PM
 */
public interface AccessKey extends Entity, Serializable {

    String getAccessKey();

    void setAccessKey(final String accessKey);

    String getSource();

    void setSource(final String source);
}
