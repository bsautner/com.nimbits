package com.nimbits.client.model.socket;

import com.nimbits.client.model.entity.Entity;

public class SocketFactory {

    public static Socket getInstance(Socket socket) {

        return new SocketModel(socket);

    }

    public static Socket getInstance(Entity anEntity, String targetApiKey, String targetUrl, String targetPath, String extraParams) {

       return new SocketModel(anEntity, targetApiKey, targetUrl, targetPath, extraParams);

    }
}
