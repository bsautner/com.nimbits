package com.nimbits.cloudplatform.server.transactions.search;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.server.NimbitsServletTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-api.xml",
        "classpath:META-INF/applicationContext-cache.xml",
        "classpath:META-INF/applicationContext-cron.xml",
        "classpath:META-INF/applicationContext-dao.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-task.xml"

})
public class EntitySearchServiceTest extends NimbitsServletTest {


    @Test
    public void testGetIndex() throws Exception {

    }

    @Test
    public void testUpdateIndex() throws Exception {

    }
    @Test
    public void testPurge() throws Exception {
        EntitySearchService.deleteAll();
        Results<ScoredDocument> results =  EntitySearchService.findEntity("point", EntityType.point);
        assertEquals(0, results.getNumberReturned());
    }



    @Test
    public void testFindEntity() throws Exception {

        Results<ScoredDocument> results =  EntitySearchService.findEntity("point", EntityType.point);
        assertEquals(1, results.getNumberReturned());
        for (ScoredDocument d : results.getResults()) {
            System.out.println(d.getId());
            System.out.println(d.getOnlyField(Parameters.name.name()).getText());
        }
    }

}
