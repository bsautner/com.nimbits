package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.admin.logging.*;
import com.nimbits.shared.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.logging.*;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 4/23/12
 * Time: 1:30 PM
 */
public class DeleteBlobTask extends HttpServlet {

    private static final Logger log = Logger.getLogger(DeleteBlobTask.class.getName());

    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        try {
            processRequest(req);

        } catch (NimbitsException e1) {
            LogHelper.logException(this.getClass(), e1);
        }
    }

    public static void processRequest(final ServletRequest req) throws NimbitsException {


        final String key = req.getParameter(Parameters.key.getText());

        if (!Utils.isEmptyString(key)) {
            final BlobKey blobKey = new BlobKey(key);
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            blobstoreService.delete(blobKey);
        }

    }


}

