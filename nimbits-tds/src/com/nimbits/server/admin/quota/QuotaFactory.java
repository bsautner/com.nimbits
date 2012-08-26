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

package com.nimbits.server.admin.quota;

import com.nimbits.client.model.email.EmailAddress;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:28 PM
 */
public class QuotaFactory {

    private QuotaFactory() {
    }

 //   private static final int MAX_EXPECTED_USERS = 64;

//    private static class MapHolder {
//        static final Map<EmailAddress, Quota> map = new HashMap<EmailAddress, Quota>(MAX_EXPECTED_USERS);
//
//        private MapHolder() {
//        }
//    }

//    public static Quota getInstance(EmailAddress emailAddress) {
//        if (MapHolder.map.containsKey(emailAddress)) {
//              return MapHolder.map.get(emailAddress);
//          }
//        else {
//              Quota quota = new QuotaImpl(emailAddress);
//              MapHolder.map.put(emailAddress, quota);
//              return quota;
//          }
//
//    }


    public static Quota getInstance(EmailAddress emailAddress) {
        return new QuotaImpl(emailAddress);

    }
    public static Quota getInstance() {
        return new QuotaImpl();

    }

}
