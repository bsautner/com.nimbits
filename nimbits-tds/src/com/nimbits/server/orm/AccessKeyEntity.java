package com.nimbits.server.orm;

import com.nimbits.client.enums.*;
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
    private String code;

    @Persistent
    private String scope;

    @Persistent
    private boolean enabled;

    private int authLevel;

    public AccessKeyEntity() {

    }

    public AccessKeyEntity(final AccessKey entity) throws NimbitsException {
        super(entity);
        this.code = entity.getCode();
        this.scope = entity.getScope();
        this.enabled = true;
        this.authLevel = entity.getAuthLevel().getCode();

    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;

    }

    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        AccessKey k = (AccessKey)update;
        this.code = k.getCode();
        this.scope = k.getScope();
        this.enabled = true;
        this.authLevel = ((AccessKey) update).getAuthLevel().getCode();
    }

    @Override
    public void validate() throws NimbitsException {
        super.validate();
        if (Utils.isEmptyString(this.code)) {
            throw new NimbitsException("Access Key must not be empty, you can delete the key if you don't want it anymore.");
        }

        if (Utils.isEmptyString(this.scope)) {
            throw new NimbitsException("Source must not be empty");
        }

        if (AuthLevel.get(this.authLevel).equals(AuthLevel.readPoint) ||
                AuthLevel.get(this.authLevel).equals(AuthLevel.readWritePoint)) {
            if (Utils.isEmptyString(this.scope)) {
                throw new NimbitsException("Auth Keys with an auth level of point, must have a target point key set");

            }
        }
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public AuthLevel getAuthLevel() {
        return AuthLevel.get(this.authLevel);
    }

    @Override
    public void setAuthLevel(AuthLevel level) {
       this.authLevel = level.getCode();
    }


}

