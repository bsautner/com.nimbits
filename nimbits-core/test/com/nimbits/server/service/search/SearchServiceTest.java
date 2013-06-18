package com.nimbits.server.service.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: bsautner
 * Date: 6/18/12
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-service.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class SearchServiceTest {

    @Resource(name="searchService")
    private SearchService searchService;


      @Test
      public void testSearch() {

          String result = searchService.processSearch("spider", "html");
          assertNotNull(result);

      }



}
