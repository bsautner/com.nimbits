package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.server.transactions.service.entity.*;
import com.nimbits.server.transactions.service.user.*;
import com.nimbits.server.transactions.service.value.*;
import com.nimbits.shared.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 1:30 PM
 */
public class DeleteOrphanedBlobTask  extends HttpServlet {

    private static final Logger log = Logger.getLogger(DeleteOrphanedBlobTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        try {
            processRequest(req);

        } catch (NimbitsException e1) {
            LogHelper.logException(this.getClass(), e1);
        }
    }

    public static void processRequest(ServletRequest req) throws NimbitsException {

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String key = req.getParameter(Parameters.key.getText());
        if (!Utils.isEmptyString(key)) {
            log.info("checking orphans: " + key);
            final List<Entity> e = EntityTransactionFactory.getDaoInstance(UserServiceFactory.getServerInstance().getAdmin())
                    .getEntityByBlobKey(new BlobKey(key));
            log.info("checked for files " + key);
            final List<ValueBlobStore> e2 = ValueTransactionFactory.getDaoInstance(null).
                    getBlobStoreByBlobKey(new BlobKey(key));
            log.info("checked for stores " + key);
            log.info(e.isEmpty() + " " + e2.isEmpty());
            if (e.isEmpty() && e2.isEmpty()) {
                //  blobstoreService.delete(new BlobKey(key));
                log.warning("Deleted orphaned blob: " + key);
            }

        }

    }
}
