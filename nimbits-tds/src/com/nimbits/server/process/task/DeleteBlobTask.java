package com.nimbits.server.process.task;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.admin.logging.LogHelper;
import com.nimbits.shared.Utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

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

