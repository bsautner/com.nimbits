package com.nimbits.server.orm.validation;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.trigger.Trigger;
import com.nimbits.client.model.user.User;
import com.nimbits.server.orm.TriggerEntity;
import com.nimbits.server.transactions.service.entity.EntityServiceFactory;
import com.nimbits.shared.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 4/19/12
 * Time: 12:02 PM
 */
public class RecursionValidation {
    private static final int MAX_RECURSION = 10;
    private static final int INT = 1024;
    private static final Logger log = Logger.getLogger(RecursionValidation.class.getName());

    private RecursionValidation() {
    }

    public static void validate(Trigger entity) throws NimbitsException {

        if (Utils.isEmptyString(entity.getTarget())) {
            throw new NimbitsException("Missing target");
        }
        if (Utils.isEmptyString(entity.getTrigger())) {
            throw new NimbitsException("Missing trigger");
        }

        List<Entity> userList = EntityServiceFactory.getInstance().getEntityByKey(entity.getOwner(),EntityType.user);

        if (userList.isEmpty()){
            throw new NimbitsException("Could not locate the owner of this trigger with the key provided!");
        }
        else {
            if (entity.isEnabled())  {
            validateAgainstExisting(userList, entity);
            }
        }


    }
    private static void validateAgainstExisting(List<Entity> user, Trigger entity) throws NimbitsException {

        Map<String, String> map = new HashMap<String, String>(INT);
        map.put(entity.getTrigger(), entity.getTarget());
        try {
            for (EntityType type : EntityType.values()) {

                Class cls = Class.forName(type.getClassName());

                if (cls.getSuperclass().equals(TriggerEntity.class)) {
                    Map<String, Entity> all = EntityServiceFactory.getInstance().getEntityMap(
                            (User) user.get(0),
                            type,
                            1000);

                    Iterable<Entity> entities = new ArrayList<Entity>(all.values());
                    for (Entity e : entities) {

                        Trigger c = (Trigger)e;
                        log.info(c.getTrigger() + ">>>" + c.getTarget());
                        if (! Utils.isEmptyString(c.getTrigger()) && ! Utils.isEmptyString(c.getTarget())) {
                        map.put(c.getTrigger(), c.getTarget());
                        }
                    }
                }
            }

            testRecursion(map,entity);
        } catch (ClassNotFoundException e) {
            throw new NimbitsException(e);
        }
    }

    private static void testRecursion(Map<String, String> map,  Trigger trigger) throws NimbitsException {

        if (map.containsKey(trigger.getTarget())) {  //then target is a calc
            String currentTrigger = trigger.getTrigger();
            int count = 0;
            while (true) {
                String currentTarget = map.get(currentTrigger);
                if (map.containsKey(currentTarget)) {
                    log.info(count + " " + currentTarget + ">>>>" + map.get(currentTarget));
                    count++;
                    if (count > MAX_RECURSION) {
                        log.warning("trigger failed validation with recursion test");
                        throw new NimbitsException("The target for this trigger is a trigger for another entity. That's ok, but the" +
                                "target for that calc is also the trigger for another, and so on for over " + MAX_RECURSION + " steps. We " +
                                "stopped checking after that, but it looks like this is an infinite loop." + " enabled=" + trigger.isEnabled());

                    }
                }
                else {
                    break;
                }
            }

        }

    }
}

