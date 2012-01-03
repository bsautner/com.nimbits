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

package com.nimbits.client.model.diagram;


import com.nimbits.client.enums.ClientType;

import java.io.Serializable;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 4:07 PM
 */
public interface Diagram extends Serializable {

    long getId();

    String getUuid();

    long getUserFk();

    String getBlobKey();

    DiagramName getName();

    long getCategoryFk();

    void setCategoryFk(final long id);

    void setReadOnly(final boolean readOnly);

    int getProtectionLevel();

    void setProtectionLevel(final int protectionLevel);

    boolean isReadOnly();

    boolean isFullScreenView();

    void setFullScreenView(final boolean fullScreenView);

    void setClientType(final ClientType clientType);

    ClientType getClientType();
}
