package com.nimbits.server.core;

import com.nimbits.client.enums.EntityType;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 12/31/11
 * Time: 10:31 AM
 */
public interface Core {

    void reportDeleteToCore(final String json, final EntityType entityType);

    //void reportCategoryUpdateToCore(final HttpServletRequest req, Category category);
    public void reportUpdateToCore(final String url, final String json, final EntityType entityType);

}
