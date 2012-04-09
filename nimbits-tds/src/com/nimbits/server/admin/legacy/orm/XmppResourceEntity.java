/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.admin.legacy.orm;

import com.google.appengine.api.datastore.*;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.xmpp.*;

import javax.jdo.annotations.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 12:36 PM
 */
@SuppressWarnings("unused")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class XmppResourceEntity  {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private com.google.appengine.api.datastore.Key key;


    @Persistent
    private String entity;

    protected XmppResourceEntity() {
    }

    public XmppResourceEntity(final Entity entity, final XmppResource resource) {
        this.key = KeyFactory.createKey(XmppResourceEntity.class.getSimpleName(), entity.getKey());
        this.entity = resource.getEntity();
    }

    public String getKey() {
        return key.getName();
    }

    public String getEntity() {
        return entity;
    }
}
