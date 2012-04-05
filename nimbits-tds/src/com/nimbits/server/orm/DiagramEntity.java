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

package com.nimbits.server.orm;


import com.google.appengine.api.blobstore.*;

import javax.jdo.annotations.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 5/20/11
 * Time: 3:59 PM
 */
@Deprecated
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class DiagramEntity  {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    public Long id;

    @Persistent
    public long userFk;

    @Persistent
    public long categoryFk;

    @Persistent
    public BlobKey blobKey;

    @Persistent
    public String name;

    @Persistent
    public String uuid;

    @Persistent
    public Integer protectionLevel;

    @Persistent
    public Date dateCreated;

    public DiagramEntity() {
    }

}
