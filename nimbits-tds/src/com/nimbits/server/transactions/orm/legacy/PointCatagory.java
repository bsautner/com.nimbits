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

package com.nimbits.server.transactions.orm.legacy;

import javax.jdo.annotations.*;

@Deprecated //only here for one time DTS on upgrade
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class PointCatagory   {

    public PointCatagory() {

    }

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Long id;

    @Persistent
    public Long userFK;

    @Persistent
    public String name;

    @Persistent
    public String description;

    @Persistent
    public String uuid;

    @Persistent
    public Integer protectionLevel;




}
