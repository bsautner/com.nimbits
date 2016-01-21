package com.nimbits.client.model.file;


import java.io.Serializable;

public class FileModel implements Serializable {

    private final String encoded;
    private final String id;

    public FileModel(String encoded, String id) {
        this.encoded = encoded;
        this.id = id;
    }

    public String getEncoded() {
        return encoded;
    }

    public String getId() {
        return id;
    }
}
