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

package com.nimbits.server.connections;

import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.connection.ConnectionRequestModel;
import com.nimbits.server.orm.ConnectionRequestEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/17/11
 * Time: 12:54 PM
 */
public class ConnectionRequestModelFactory {


    public static ConnectionRequestModel CreateConnectionRequestModel(final ConnectionRequestEntity c) {
        return new ConnectionRequestModel(c);
    }

    public static List<Connection> CreateConnectionRequestModels(final List<ConnectionRequestEntity> cl) {
        final List<Connection> retObj = new ArrayList<Connection>();
        for (final ConnectionRequestEntity c : cl) {
            retObj.add(new ConnectionRequestModel(c));
        }
        return retObj;
    }

}
