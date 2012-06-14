package com.nimbits.server.api.integration;

import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.Parameters;
import com.nimbits.client.enums.ProtectionLevel;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModelFactory;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.http.HttpCommonFactory;
import org.junit.Test;

import java.util.UUID;


public class EntityIntegrationTest {
   private final static String TEST_PATH = "http://localhost:8080/service/entity";
    @Test
    public void testPost() throws NimbitsException {
        EntityName name = CommonFactoryLocator.getInstance().createName("foo", EntityType.point);

        Entity e = EntityModelFactory.createEntity(name, "local unit test", EntityType.point, ProtectionLevel.everyone,
                "test@example.com", "test@example.com", UUID.randomUUID().toString());

        String entity = GsonFactory.getInstance().toJson(e);

        final String params =  Parameters.entity.getText() + '=' + entity
                + '&' + Parameters.action.getText() + '=' + Action.update.getCode()
                + '&' + Parameters.instance.getText() + '=' + "http://localhost:8081";


        final String response =
                HttpCommonFactory.getInstance().doPost(TEST_PATH, params);



    }



}
