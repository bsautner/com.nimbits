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

package com.nimbits.client.model.diagram.impl;

import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.impl.CommonIdentifierImpl;
import com.nimbits.client.model.diagram.DiagramName;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 8/15/11
 * Time: 3:14 PM
 */
public class DiagramNameImpl extends CommonIdentifierImpl implements DiagramName, Serializable {

    private static final long serialVersionUID = Const.DEFAULT_SERIAL_VERSION;

    public DiagramNameImpl(final String value) {
        super(value);

    }

    protected DiagramNameImpl() {
        super();
    }
}
