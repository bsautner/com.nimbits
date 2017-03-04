package com.nimbits.server;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import java.util.Properties;

@Component
public class PMF {

    @Value("${system.database.url}")
    private String url;

    @Value("${system.database.login}")
    private String login;

    @Value("${system.database.password}")
    private String password;

    @Value("${system.database.driver}")
    private String driver;


    private static PersistenceManagerFactory pmf;

    public PMF() {
    }

    public PersistenceManagerFactory get() {
        if (pmf == null) {
            Properties properties = new Properties();
            properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
            properties.setProperty("javax.jdo.option.ConnectionURL", url);
            properties.setProperty("javax.jdo.option.ConnectionDriverName", driver);
            properties.setProperty("javax.jdo.option.ConnectionUserName", login);
            properties.setProperty("javax.jdo.option.ConnectionPassword", password);
            properties.setProperty("org.jpox.identifier.case", "PreserveCase");
            properties.setProperty("datanucleus.schema.autoCreateAll", "true");
            properties.setProperty("datanucleus.query.sql.allowAll", "true");
            properties.setProperty("org.jpox.autoCreateSchema", "true");
            pmf =JDOHelper.getPersistenceManagerFactory(properties);

        }
        return pmf;

    }

}
