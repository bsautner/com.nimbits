package com.nimbits.client.model.system;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class SystemDetailsModel implements SystemDetails, Serializable {

    @Expose
    private String version;
    @Expose
    private boolean isGAE;

    public SystemDetailsModel(String version, boolean isGAE) {
        this.version = version;
        this.isGAE = isGAE;
    }

    public SystemDetailsModel() {
    }

    @Override
    public Boolean isGAE() {
        return this.isGAE;
    }

    @Override
    public String getVersion() {
        return this.version;
    }
}
