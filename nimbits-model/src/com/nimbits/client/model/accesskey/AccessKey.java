package com.nimbits.client.model.accesskey;

import com.nimbits.client.enums.*;
import com.nimbits.client.model.entity.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:21 PM
 */
public interface AccessKey extends Entity, Serializable {

    String getCode();

    void setCode(final String code);

    String getScope();

    void setScope(final String scope);

    AuthLevel getAuthLevel();

    void setAuthLevel(AuthLevel level);



}
