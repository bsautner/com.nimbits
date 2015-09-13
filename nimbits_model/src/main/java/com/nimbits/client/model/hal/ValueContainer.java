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
    private EmbeddedValues embedded;

    @Expose
    private Value snapshot;


    public ValueContainer() {
    }

    public ValueContainer(Links links, EmbeddedValues embedded, Value snapshot) {
        this.links = links;
        this.embedded = embedded;
        this.snapshot = snapshot;
    }

    public Links getLinks() {
        return links;
    }

    public EmbeddedValues getEmbedded() {
        return embedded;
    }

    public Value getSnapshot() {
        return snapshot;
    }
}
