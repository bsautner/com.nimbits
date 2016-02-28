/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.orm;

import com.nimbits.client.common.Utils;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.socket.Socket;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;


@SuppressWarnings("unused")
@PersistenceCapable
public class SocketEntity extends EntityStore implements Socket {


    @Persistent
    private String targetApiKey;
    @Persistent
    private String targetUrl;
    @Persistent
    private String targetPath;
    @Persistent
    private String extraParams;


    protected SocketEntity() {
    }

    public SocketEntity(final Socket aSocket) {
        super(aSocket);
        this.targetApiKey = aSocket.getTargetApiKey();
        this.targetUrl = aSocket.getTargetUrl();
        this.targetPath = aSocket.getTargetPath();
        this.extraParams = aSocket.getExtraParams();


    }


    @Override
    public void update(final Entity aSocket) {
        super.update(aSocket);
        final Socket update = (Socket) aSocket;
        this.targetApiKey = update.getTargetApiKey();
        this.targetUrl = update.getTargetUrl();
        this.targetPath = update.getTargetPath();
        this.extraParams = update.getExtraParams();
    }


    @Override
    public String getTargetApiKey() {
        return targetApiKey;
    }

    @Override
    public String getTargetUrl() {
        return targetUrl;
    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }

    @Override
    public String getExtraParams() {
        return extraParams;
    }


    @Override
    public void validate(User user) {
        super.validate(user);

        if (Utils.isEmptyString(this.targetApiKey)) {
            throw new IllegalArgumentException("Please supply an API Key");
        }
        if (Utils.isEmptyString(this.targetUrl)) {
            throw new IllegalArgumentException("Please supply a url to the socket host server.");
        }


    }


}
