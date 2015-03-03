package com.nimbits.server.api;

import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.valueblobstore.ValueBlobStore;
import com.nimbits.server.io.BlobStore;
import com.nimbits.server.transaction.entity.dao.EntityDao;
import com.nimbits.server.transaction.user.dao.UserDao;
import com.nimbits.server.transaction.value.dao.ValueDao;
import com.nimbits.server.transaction.value.service.ValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class GCSServlet extends HttpServlet {

    @Autowired
    private BlobStore blobStore;

    @Autowired
    private EntityDao entityDao;

    @Autowired
    private ValueDao valueDao;

    @Autowired
    private UserDao userDao;


    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);


    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         PrintWriter w = resp.getWriter();

         List<ValueBlobStore> s = blobStore.getLegacy();
        if (s.isEmpty()) {
            w.print("DONE!");
        }
        else {
                ValueBlobStore v = s.get(0);
                w.println("\n");
                w.print(v.getId() + " ");
                w.print(v.getEntity());
                Point point = entityDao.getPoint(v.getEntity());
                w.print(" Point Exists: " + (point != null));

                if (point == null) {
                    blobStore.deleteBlobs(s);
                    blobStore.deleteBlobStoreEntity(s);
                    w.print("Deleted data for missing point " + v.getEntity());
                }
                else {

                    if (point.getName().getValue().contains("_api_counter")) {
                        entityDao.deletePoint(point);
                        blobStore.deleteBlobs(s);
                        blobStore.deleteBlobStoreEntity(s);
                        w.print(" deleted garbage");
                    } else {
                        List<Value> values = blobStore.upgradeStore(point, v);
                        w.print(" Upgrading Values: " + values.size());
                        valueDao.recordValues(point, values);
                    }


            }
        }

    }
}
