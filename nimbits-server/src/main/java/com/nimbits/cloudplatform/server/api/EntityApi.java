package com.nimbits.cloudplatform.server.api;

import com.nimbits.cloudplatform.client.enums.Action;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.ExportType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import com.nimbits.cloudplatform.client.model.entity.EntityModel;
import com.nimbits.cloudplatform.server.gson.GsonFactory;
import com.nimbits.cloudplatform.server.transactions.entity.EntityHelper;
import com.nimbits.cloudplatform.server.transactions.entity.EntityServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service("entityApi")
public class EntityApi extends ApiServlet implements org.springframework.web.HttpRequestHandler {

    //TODO - update, delete, create all handled with post with json in message body

    public static final String SERVER_RESPONSE = "SERVER_RESPONSE";
    public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
    public static final String CREATING_ENTITY = "Creating Entity";
    private String json;
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp)  {


        if (isPost(req)) {
            json = getParam(Parameters.json);
            if (StringUtils.isEmpty(json)) {
                json = getContent(req);
            }

            doPost(req, resp);
        } else {
            doGet(req, resp);
        }

    }

    @Override
    public void doGet(final HttpServletRequest req,
                      final HttpServletResponse resp)   {




        try {
            final PrintWriter out = resp.getWriter();

            doInit(req, resp, ExportType.json);



            if (user != null && !user.isRestricted()) {

                try {
                    EntityType entityType = EntityType.valueOf(getParam(Parameters.type));

                    List<Entity> sample = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.id), entityType);
                    if (sample.isEmpty()) {
                        resp.addHeader("error details", "entity not found");
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                    } else {
                        Entity e = sample.get(0);
                        String outJson = GsonFactory.getInstance().toJson(e);
                        out.print(outJson);

                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                } catch (Exception e) {
                    resp.addHeader("error details", e.getMessage());
                    resp.addHeader("ERROR", e.getMessage());
                }


            } else {
                // out.print(Words.WORD_FALSE);
                resp.addHeader("error details", "you're not logged in and didn't supply a valid key");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);

            }

            out.close();
        } catch (Exception e) {
            resp.addHeader("error details", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {


        doInit(req, resp, ExportType.json);

        List<Entity> entityList = null;
        Action action = Action.valueOf(getParam(Parameters.action));
        if (action != null && user != null && !user.isRestricted()) {

            switch (action) {

                case delete:
                    deleteEntity(resp);
                    break;
                case create:
                    entityList = createEntity(resp);
                    break;
                case update:
                    entityList = updateEntity(resp);
                    break;
                case createmissing:
                    entityList = addMissingEntity(resp);

            }
            if (entityList != null && ! entityList.isEmpty()) {
                final PrintWriter out;
                try {
                    out = resp.getWriter();

                    String outJson = GsonFactory.getInstance().toJson(entityList.get(0));
                    out.print(outJson);

                    //resp.setStatus(HttpServletResponse.SC_OK);
                    out.close();
                } catch (IOException e) {

                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }


        }

    }

    private List<Entity> createEntity(HttpServletResponse resp)  {


        if (!StringUtils.isEmpty(json)) {
            Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getKey())) {
                List<Entity> sample = EntityServiceImpl.getEntityByKey(user, sampleEntity.getKey(), sampleEntity.getEntityType());
                if (!sample.isEmpty()) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    resp.addHeader("error details", "The entity you're trying to create already exists");
                    return Collections.emptyList();
                   // throw new IllegalArgumentException("The entity you're trying to create already exists");
                } else {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    return addUpdateUpscaledEntity();
                }


            } else {

                resp.setStatus(HttpServletResponse.SC_OK);
                return addUpdateUpscaledEntity();
            }

        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.addHeader("error details", "invalid json");
            throw new IllegalArgumentException("Invalid JSON");

        }


    }

    private List<Entity> addUpdateUpscaledEntity( ) {
        Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
        Class cls = EntityHelper.getClass(entity.getEntityType());

        Object up = GsonFactory.getInstance().fromJson(json, cls);
        List<Entity> list = new ArrayList<Entity>(1);
        list.add((Entity) up);

        return EntityServiceImpl.addUpdateEntity(user, list);
    }

    private List<Entity> updateEntity(HttpServletResponse resp)  {



        if (!StringUtils.isEmpty(json)) {
            Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getKey())) {

                List<Entity> sample = EntityServiceImpl.getEntityByKey(user, sampleEntity.getKey(), sampleEntity.getEntityType());
                if (!sample.isEmpty()) {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    return addUpdateUpscaledEntity();
                } else {
                    resp.addHeader("error details", "entity not found");
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    throw new IllegalArgumentException("Entity Not Found");
                }

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new IllegalArgumentException("Entity Not Found");
            }

        } else {
            resp.addHeader("error details", "invalid json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException("Invalid JSON");

        }

    }

    private List<Entity> addMissingEntity(HttpServletResponse resp)  {



        if (!StringUtils.isEmpty(json)) {
            Entity sampleEntity = GsonFactory.getInstance().fromJson(json, EntityModel.class);

            if (sampleEntity != null && !StringUtils.isEmpty(sampleEntity.getName().getValue())) {
                log.info("user:" + (user == null));
                log.info("json:" + json);
                log.info("sampleEntity.getName():" + sampleEntity.getName().getValue());
                log.info("ampleEntity.getEntityType():" + sampleEntity.getEntityType());
                List<Entity> sample = EntityServiceImpl.getEntityByName(user, sampleEntity.getName(), sampleEntity.getEntityType());
                if (!sample.isEmpty()) {
                    log.info("found" + json);
                    resp.addHeader(SERVER_RESPONSE, ENTITY_ALREADY_EXISTS);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    return sample;
                } else {
                    log.info("did not found" + json);
                    resp.addHeader(SERVER_RESPONSE, CREATING_ENTITY);
                    return addUpdateUpscaledEntity();
                    //return EntityServiceImpl.addUpdateSingleEntity(user, sampleEntity);

                }

            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new IllegalArgumentException("Entity Not Found");
            }

        } else {
            resp.addHeader("error details", "invalid json");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException("Invalid JSON");

        }

    }
    private void deleteEntity(HttpServletResponse resp)  {


        EntityType entityType = EntityType.valueOf(getParam(Parameters.type));
        List<Entity> sample = EntityServiceImpl.getEntityByKey(user, getParam(Parameters.id), entityType);

        if (sample.isEmpty()) {
            resp.addHeader("error details", "entity not found");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        } else {
            Entity e = sample.get(0);

            EntityServiceImpl.deleteEntity(user, sample);
            resp.setStatus(HttpServletResponse.SC_OK);


        }
    }
}
