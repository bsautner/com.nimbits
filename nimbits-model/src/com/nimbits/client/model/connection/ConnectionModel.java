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

package com.nimbits.client.model.connection;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;

import java.io.Serializable;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/9/12
 * Time: 2:17 PM
 */
public class ConnectionModel extends EntityModel implements Serializable, Connection {

    private static final long serialVersionUID = 943183302829417495L;

    protected ConnectionModel() {

    }

    public ConnectionModel(final Entity entity) throws NimbitsException {
        super(entity);
    }
}
