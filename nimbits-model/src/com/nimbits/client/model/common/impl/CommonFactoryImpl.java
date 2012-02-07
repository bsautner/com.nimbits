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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.model.common.impl;

import com.nimbits.client.exception.NimbitsRuntimeException;
import com.nimbits.client.model.common.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.email.impl.EmailAddressImpl;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.entity.EntityNameImpl;


/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/6/11
 * Time: 11:09 AM
 */
public class CommonFactoryImpl implements CommonFactory {


    @Override
    public EmailAddress createEmailAddress(final String value) {
        if (value == null) {
            throw new NimbitsRuntimeException("Email can not be null");
        }
        return new EmailAddressImpl(value);

    }



    @Override
    public EntityName createName(final String value) {
        return new EntityNameImpl(value);
    }

}
