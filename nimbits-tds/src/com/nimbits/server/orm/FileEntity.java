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

package com.nimbits.server.orm;

import com.google.appengine.api.blobstore.BlobKey;
import com.nimbits.client.common.Utils;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.file.File;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/8/12
 * Time: 10:53 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class FileEntity extends EntityStore implements File {

    @Persistent
    private BlobKey blobKey;

    public FileEntity(final File entity) throws NimbitsException {
        super(entity);
        if (! Utils.isEmptyString(entity.getBlobKey()))  {
            this.blobKey = new BlobKey(entity.getBlobKey());
        }

    }

    @Override
    public String getBlobKey() {
        return blobKey != null ? this.blobKey.getKeyString() : null;
    }

    @Override
    public void setBlobKey(final String blobKey) {
        if (! Utils.isEmptyString(blobKey)) {
            this.blobKey = new BlobKey(blobKey);
        }

    }

    @Override
    public void update(Entity update) throws NimbitsException {
        super.update(update);
        File f = (File) update;
        if (! Utils.isEmptyString(f.getBlobKey())) {
            this.blobKey = new BlobKey(f.getBlobKey());
        }
    }

    @Override
    public void validate() throws NimbitsException {
        super.validate();
        if (this.blobKey == null || Utils.isEmptyString(this.blobKey.getKeyString())) {
            throw new NimbitsException("Empty blobkey");
        }
    }
}