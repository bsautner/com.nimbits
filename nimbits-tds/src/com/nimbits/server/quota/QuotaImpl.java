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

package com.nimbits.server.quota;

import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.UserMessages;
import com.nimbits.client.enums.SettingType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;
import com.nimbits.server.counter.CounterFactory;
import com.nimbits.server.dao.counter.ShardedCounter;
import com.nimbits.server.settings.SettingsServiceFactory;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:27 PM
 */
public class QuotaImpl implements Quota {
 //   private final User user;
    ShardedCounter counter;
    public QuotaImpl(final User user) {
       // this.user = user;
        counter = getOrCreateCounter(user.getEmail());

    }

    @Override
    public void incrementCounter() throws NimbitsException {

        counter.increment();
        if (counter.getCount() > Const.MAX_DAILY_QUOTA) {
            if (SettingsServiceFactory.getInstance().getBooleanSetting(SettingType.quotaEnabled)) {
                throw new NimbitsException(UserMessages.ERROR_QUOTA_EXCEEDED);
                //todo here is where we charge em;
            }

        }
    }

    @Override
    public void resetCounter() throws NimbitsException {
      final int count = counter.getCount();
      counter.increment(count * -1);
    }

    @Override
    public int getCount() throws NimbitsException {
        return counter.getCount();
    }


    private static ShardedCounter getOrCreateCounter(final EmailAddress email) {
        final CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getCounter(email.getValue());
        if (counter == null) {
            counter = factory.createCounter(email.getValue());
            counter.addShard();

        }
        return counter;
    }
}
