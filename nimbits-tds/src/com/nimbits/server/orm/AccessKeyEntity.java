package com.nimbits.server.orm;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.accesskey.*;
import com.nimbits.shared.*;

import javax.jdo.annotations.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/16/12
 * Time: 2:39 PM
 */

@PersistenceCapable()
public class AccessKeyEntity extends EntityStore implements AccessKey {

    @Persistent
    private Date createDate;

    @Persistent
    private String accessKey;

    @Persistent
    private String source;

    @Persistent
    private boolean enabled;

    public AccessKeyEntity() {

    }

    public AccessKeyEntity(final AccessKey entity) throws NimbitsException {
        super(entity);
        this.accessKey = entity.getAccessKey();
        this.source = entity.getSource();
        this.enabled = true;
        createDate = new Date();
    }

    @Override
    public String getAccessKey() {
        return this.accessKey;
    }

    @Override
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;

    }

    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        AccessKey k = (AccessKey)update;
        this.accessKey = k.getAccessKey();
        this.source = k.getSource();
        this.enabled = true;
    }

    @Override
    public void validate() throws NimbitsException {
        super.validate();
        if (Utils.isEmptyString(this.accessKey)) {
            throw new NimbitsException("Access Key must not be empty, you can delete the key if you don't want it anymore.");
        }

        if (Utils.isEmptyString(this.source)) {
            throw new NimbitsException("Source must not be empty");
        }
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

}

