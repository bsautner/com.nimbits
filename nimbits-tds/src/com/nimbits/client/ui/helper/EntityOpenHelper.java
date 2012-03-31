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

package com.nimbits.client.ui.helper;

import com.google.gwt.user.client.Window;
import com.nimbits.client.constants.Const;
import com.nimbits.client.constants.Path;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 3/15/12
 * Time: 9:44 AM
 */
public class EntityOpenHelper {

    public static void showEntity(final Entity entity) throws NimbitsException {
        if (isSVG(entity)) {
            openNewEntityWindow(entity);
        }
        else if (entity.getEntityType().equals(EntityType.file)) {
           showBlob(entity);
        }
        else {
            openNewEntityWindow(entity);
        }
    }

    private static void openNewEntityWindow(Entity entity) throws NimbitsException {
        Window.open("/" + "?" + Parameters.uuid.getText() + "=" + entity.getEntity(), entity.getName().getValue(), "");
    }

    public static boolean isSVG(final Entity entity) throws NimbitsException {
        return entity.getName().getValue().toLowerCase().endsWith(Const.FILE_TYPE_SVG);
    }

    public  static void showBlob(final Entity entity) throws NimbitsException {
        final String resourceUrl = Path.PATH_BLOB_SERVICE + "?" + Parameters.blobkey.getText() + "=" + entity.getBlobKey();
        Window.open(resourceUrl, entity.getName().getValue(), "");
    }


}
