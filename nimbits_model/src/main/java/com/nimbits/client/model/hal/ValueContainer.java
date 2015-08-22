package com.nimbits.client.model.hal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nimbits.client.model.value.Value;

import java.io.Serializable;

public class ValueContainer implements Serializable {

    //HAL
    @SerializedName("_links")
    @Expose
    private Links links;
    @SerializedName("_embedded")
    @Expose
    private Embedded embedded;

    @Expose
    private Value snapshot;


    public ValueContainer(Links links, Embedded embedded, Value snapshot) {
        this.links = links;
        this.embedded = embedded;
        this.snapshot = snapshot;
    }
}
