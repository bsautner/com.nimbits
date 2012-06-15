package com.nimbits.server.service.entity.impl;

import com.nimbits.client.enums.Action;
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

@Transactional
@Service("entityService")
public class EntityServiceImpl implements EntityService {

    @Resource(name="entityDao")
    private EntityJPATransactions entityDao;

    @Override
    public void processEntity(final String entityJson, final String actionText, final String instanceURL) throws NimbitsException {
        if (StringUtils.isNotEmpty(entityJson) && StringUtils.isNotEmpty(actionText) && StringUtils.isNotEmpty(instanceURL)) {

            Entity entity = GsonFactory.getInstance().fromJson(entityJson, EntityModel.class);
            Action action = Action.get(actionText);
            if (action != null && action.equals(Action.update)) {
                entityDao.addUpdateEntity(entity, instanceURL);
            }
            else if (action != null && action.equals(Action.delete)) {
                entityDao.deleteEntityByUUID(entity.getUUID());
            }


        }

    }
}
