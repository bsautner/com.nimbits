package com.nimbits.client.ui.helper;

import com.google.gwt.user.client.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.*;

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
