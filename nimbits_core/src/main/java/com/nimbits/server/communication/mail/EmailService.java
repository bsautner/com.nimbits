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

package com.nimbits.server.communication.mail;

import com.nimbits.client.model.connection.Connection;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.subscription.Subscription;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;


public interface EmailService {


    void sendEmail(EmailAddress emailAddress,
                   String message,
                   String subject);

    void sendAlert(Entity entity,
                   Point point,
                   EmailAddress emailAddress,
                   Value value, Subscription subscription);

    void sendConnectionRequest(User user, Connection c);

    void sendConnectionRequestApprovalNotification(Connection c);

    void sendPasswordRecovery(String email, String token);
}
