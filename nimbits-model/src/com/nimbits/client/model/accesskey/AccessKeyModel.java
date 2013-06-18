package com.nimbits.client.model.accesskey;

import com.nimbits.client.enums.*;
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

    private String code;
    private String scope;
    private int authLevel;

    @SuppressWarnings("unused")
    private AccessKeyModel() {
    }

    public AccessKeyModel(final AccessKey anEntity) throws NimbitsException {
        super(anEntity);
        this.code = anEntity.getCode();
        this.scope = anEntity.getScope();
        this.authLevel = anEntity.getAuthLevel().getCode();

    }

    @Override
    public String getCode() {
        return code;
    }

    public AccessKeyModel(final Entity anEntity, final String code, final String scope, final AuthLevel level) throws NimbitsException {
        super(anEntity);
        this.code = code;
        this.scope = scope;
        this.authLevel = level.getCode();
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getScope() {
        return this.scope;
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
