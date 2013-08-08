package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.value.Value;
import com.nimbits.cloudplatform.client.model.value.impl.ValueModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import com.nimbits.cloudplatform.server.transactions.value.ValueTransaction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Author: Benjamin Sautner
 * Date: 12/28/12
 * Time: 4:11 PM
 */
//TODO accept JSON in body instead of param


@Service("valueApi")
public class ValueApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {
    final Logger log = Logger.getLogger(ValueApi.class.getName());

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp)  {

        if (isPost(req)) {
            doPost(req, resp);
        } else {
            doGet(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)   {

        log.info("posting value");

        String json = req.getParameter(Parameters.json.name());
        log.info(json);


        try {
            final PrintWriter out = resp.getWriter();



            doInit(req, resp, ExportType.json);
            if (StringUtils.isEmpty(json)) {
                json = getContent(req);
            }
            if (user != null && !user.isRestricted()) {

                List<Entity> entitySample = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.id), EntityType.point);
                if (entitySample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {

                    Value value = GsonFactory.getInstance().fromJson(json, ValueModel.class);
                    if (value.getTimestamp().getTime() == 0) {
                        value = ValueModel.getInstance(value, new Date());
                    }
                    Value recorded = ValueTransaction.recordValue(user, entitySample.get(0), value);
                    // log.info("redorded" + " " + value.getDoubleValue());
                    resp.setStatus(HttpServletResponse.SC_OK);
                    String respString = GsonFactory.getInstance().toJson(recorded, ValueModel.class);
                    out.print(respString);
                    // log.info(respString);
                    out.close();

                }


            } else {
                // out.print(Words.WORD_FALSE);
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }


    }



    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp)  {




        try {
            final PrintWriter out = resp.getWriter();
            doInit(req, resp, ExportType.json);


            if (user != null && !user.isRestricted()) {

                List<Entity> entitySample = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.id), EntityType.point);
                if (entitySample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                } else {
                    List<Value> sample = ValueTransaction.getCurrentValue(entitySample.get(0));
                    if (sample.isEmpty()) {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        String json = GsonFactory.getInstance().toJson(sample.get(0), ValueModel.class);
                        out.print(json);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }


                }


            } else {
                // out.print(Words.WORD_FALSE);
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }
            out.close();
        } catch (Exception e) {
            resp.addHeader("ERROR", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


        }


    }

}