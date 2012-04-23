package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.io.blob.*;
import com.nimbits.shared.*;

import javax.servlet.http.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 1:30 PM
 */
public class DeleteOrphanedBlobTask  extends HttpServlet {

//    private static final Logger log = Logger.getLogger(UpdatePointStatsTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
         String key = req.getParameter(Parameters.key.getText());
        try {
            if (Utils.isEmptyString(key)) {
                BlobStoreFactory.getInstance().deleteOrphans(null);
            }
            else {
                BlobStoreFactory.getInstance().deleteOrphans(new BlobKey(key));
            }

        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }

    }
}
