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

package com.nimbits.client.model.webhook;

import com.nimbits.client.model.UrlContainer;
import com.nimbits.client.model.entity.Entity;

import java.io.Serializable;

public interface WebHook extends Entity, Serializable {

    HttpMethod getMethod();

    DataChannel getPathChannel();

    void setPathChannel(DataChannel dataChannel);

    DataChannel getBodyChannel();

    void setBodyChannel(DataChannel dataChannel);

    UrlContainer getUrl();

    boolean isEnabled();

    void setMethod(HttpMethod method);

    void setUrl(UrlContainer url);

    void setEnabled(boolean enabled);

    String getDownloadTarget();

    void setDownloadTarget(String key);


}
