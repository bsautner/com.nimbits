package com.nimbits.server.quota;

import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.counter.*;
import com.nimbits.server.dao.counter.*;
import com.nimbits.server.settings.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/29/12
 * Time: 3:27 PM
 */
public class QuotaImpl implements Quota {
 //   private final User user;
    ShardedCounter counter;
    public QuotaImpl(User user) {
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
      int count = counter.getCount();
      counter.increment(count * -1);
    }

    @Override
    public int getCount() throws NimbitsException {
        return counter.getCount();
    }


    private ShardedCounter getOrCreateCounter(final EmailAddress email) {
        CounterFactory factory = new CounterFactory();
        ShardedCounter counter = factory.getCounter(email.getValue());
        if (counter == null) {
            counter = factory.createCounter(email.getValue());
            counter.addShard();

        }
        return counter;
    }
}
