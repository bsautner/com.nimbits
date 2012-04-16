package com.nimbits.client.model.accesskey;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:23 PM
 */
public class AccessKeyModel extends EntityModel implements AccessKey, Serializable {

    private String accessKey;
    private String source;

    @SuppressWarnings("unused")
    private AccessKeyModel() {
    }

    public AccessKeyModel(final AccessKey anEntity) throws NimbitsException {
        super(anEntity);
        this.accessKey = anEntity.getAccessKey();
        this.source = anEntity.getSource();
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    public AccessKeyModel(final Entity anEntity, final String accessKey, final String source) throws NimbitsException {
        super(anEntity);
        this.accessKey = accessKey;
        this.source = source;
    }

    @Override
    public void setAccessKey(final String accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    public String getSource() {
       return this.source;
    }

    @Override
    public void setSource(final String source) {
      this.source = source;
    }
}
