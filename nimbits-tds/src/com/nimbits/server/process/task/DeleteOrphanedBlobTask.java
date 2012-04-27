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


        String key = req.getParameter(Parameters.key.getText());
        BlobKey blobKey = new BlobKey(key);
        if (!Utils.isEmptyString(key)) {
        checkFile(blobKey, false);
        }

    }

    public static void checkFile(BlobKey blobKey, boolean recursive) throws NimbitsException {



            final List<Entity> e = EntityTransactionFactory.getDaoInstance(UserServiceFactory.getServerInstance().getAdmin())
                    .getEntityByBlobKey(blobKey);

            final List<ValueBlobStore> e2 = ValueTransactionFactory.getDaoInstance(null).
                    getBlobStoreByBlobKey(blobKey);

            if (e.isEmpty() && e2.isEmpty()) {
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                blobstoreService.delete(blobKey);
                log.warning("Deleted orphaned blob: " + blobKey.getKeyString());
            }
            Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfosAfter(blobKey);

            if  (iterator.hasNext()){
                final BlobInfo i = iterator.next();
                if (recursive){
                    checkFile(i.getBlobKey(), recursive);
                }
                else {
                    TaskFactory.getInstance().startDeleteOrphanedBlobTask(i.getBlobKey());
                }

            }
        }
    }

