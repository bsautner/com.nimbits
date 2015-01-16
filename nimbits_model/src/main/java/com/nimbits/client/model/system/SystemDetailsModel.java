package com.nimbits.client.model.system;

import java.io.Serializable;

public class SystemDetailsModel implements SystemDetails, Serializable {


    private String version;
    private boolean isGAE;

    public SystemDetailsModel(String version, boolean isGAE) {
        this.version = version;
        this.isGAE = isGAE;
    }

    public SystemDetailsModel() {
    }

    @Override
    public boolean isGAE() {
        return this.isGAE;
    }

    @Override
    public String getVersion() {
        return this.version;
    }
}
