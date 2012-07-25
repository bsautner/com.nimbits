/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.external.facebook;

import java.io.*;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 4/13/11
 * Time: 11:40 AM
 */
public class FacebookUser implements Serializable {

    private Long id;

    private static final long serialVersionUID = 1L;

    public FacebookUser() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    private String name;

    public String getName() {
        return name;
    }


}


