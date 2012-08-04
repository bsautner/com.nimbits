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

package com.nimbits.server.communication.email;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;

public interface EmailService {
    void sendEmail(final EmailAddress email, final String message);

    void sendEmail(final EmailAddress email, final String message, final String subject);

    void sendEmail(final EmailAddress fromEmail,
                   final EmailAddress emailAddress,
                   final String message,
                   final String subject);

    void sendAlert(final Entity entity, final Point point, final EmailAddress email, final Value value) throws NimbitsException;
}
