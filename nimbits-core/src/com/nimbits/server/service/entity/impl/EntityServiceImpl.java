package com.nimbits.server.service.entity.impl;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.service.entity.EntityService;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@Transactional
@Service("entityService")
public class EntityServiceImpl implements EntityService {
    private static final Logger log = Logger.getLogger(EntityServiceImpl.class.getName());

    @Resource(name="entityDao")
    private EntityJPATransactions entityDao;

    @Override
    public void processEntity(final String entityJson, final String actionText, final String instanceURL) throws NimbitsException {

        log.info("processing entity");
        log.info(entityJson);
        log.info(actionText);
        log.info(instanceURL);

        if (StringUtils.isNotEmpty(entityJson) && StringUtils.isNotEmpty(actionText) && StringUtils.isNotEmpty(instanceURL)) {
            log.info("doing update");
            Entity entity = null;
            try {
                entity = GsonFactory.getInstance().fromJson(entityJson, EntityModel.class);
            }
            catch (com.google.gson.JsonSyntaxException ex) {
                log.severe(ex.getMessage());
            }

            log.info("Created entity" + (entity ==null));
            Action action = Action.get(actionText);
            if (! entity.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                action = Action.delete;
            }
            log.info("Created action" + (action ==null));
            if (entity != null)  {
                if ((action != null) && action.equals(Action.update)) {
                    log.info("calling addUpdateEntity");
                    entityDao.addUpdateEntity(entity, instanceURL);
                }
                else if (action != null && action.equals(Action.delete)) {
                    log.info("calling deleteEntityByUUID");
                    entityDao.deleteEntityByUUID(entity.getUUID());
                }
                else {
                    log.severe("error in post");
                }
            }
            else  {
                log.severe("Entity was null");
            }
        }

    }

    @Override
    public void processLocation(String entityJson, String location) throws NimbitsException {
        log.info("processing location");
        log.info(entityJson);
        log.info(location);


        if (StringUtils.isNotEmpty(entityJson) && StringUtils.isNotEmpty(location)) {
            log.info("doing update");
            Entity entity = null;
            try {
                entity = GsonFactory.getInstance().fromJson(entityJson, EntityModel.class);
            }
            catch (com.google.gson.JsonSyntaxException ex) {
                log.severe(ex.getMessage());
            }



            if (entity != null)  {
                log.info("doing update with good entity and location" + location + " " + entity.getUUID());
                entityDao.updateLocation(entity, location);

            }
            else  {
                log.severe("Entity was null");
            }
        }

    }

    @Override
    public List<String[]> getLocations() {
        return entityDao.getLocations();
    }


}
