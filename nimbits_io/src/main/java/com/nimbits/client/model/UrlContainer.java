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

package com.nimbits.client.model;

import java.io.Serializable;


public class UrlContainer implements Serializable {

    private String url;


    public static UrlContainer getInstance(final String url) {
        final UrlContainer instance = new UrlContainer();
        instance.setUrl(url);
        return instance;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }

    public static UrlContainer combine(final UrlContainer base, final UrlContainer... containers) {
        final StringBuilder sb = new StringBuilder();
        if (base != null) {
            sb.append(base.getUrl());
        }

        for (final UrlContainer c : containers) {
            sb.append(c);

        }
        return UrlContainer.getInstance(sb.toString());


    }


}
