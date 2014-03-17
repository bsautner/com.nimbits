/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.communication.email;


import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.value.Value;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;


public class EmailServiceImpl implements EmailService {


     private final PersistenceManagerFactory pmf;

    public EmailServiceImpl(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }



    @Override
    public void sendEmail(final EmailAddress emailAddress, final String message) {

    }


    @Override
    public void sendEmail(final EmailAddress emailAddress,
                          final String message,
                          final String subject) {

    }

    @Override
    public void sendEmail(final EmailAddress fromEmail,
                          final EmailAddress emailAddress,
                          final String message,
                          final String subject) {

    }

    @Override
    public void sendAlert(final Entity entity,
                          final Point point,
                          final EmailAddress emailAddress,
                          final Value value, Subscription subscription)  {

    }


}
