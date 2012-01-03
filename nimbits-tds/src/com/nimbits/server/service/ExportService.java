package com.nimbits.server.service;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.nimbits.client.model.Const;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class ExportService extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        final String pointNameParam = req.getParameter(Const.PARAM_POINT);
        final String countStr = req.getParameter(Const.PARAM_COUNT);
        String segStr = req.getParameter(Const.PARAM_SEGMENT);
        final String startDate = req.getParameter(Const.PARAM_START_DATE);
        final String endDate = req.getParameter(Const.PARAM_END_DATE);

        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = resp.getWriter();

        out.println("<html><body>hello</body></html>");
        out.flush();
        //   out.close();

    }

    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter(Const.PARAM_BLOB_KEY));
        blobstoreService.serve(blobKey, resp);
    }
}
