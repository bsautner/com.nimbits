package com.nimbits.server.transactions.search;

import com.nimbits.server.orm.JpaSearchLog;
import com.nimbits.server.transactions.dao.search.SearchLogTransactions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: benjamin
 * Date: 5/29/12
 * Time: 7:00 AM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "classpath:META-INF/applicationContext.xml",
        "classpath:META-INF/applicationContext-daos.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class SearchLogDaoImplTest  {

    private SearchLogTransactions searchLogTransactions;

    @Resource(name="searchDao")
    public void setInstanceTransactions(SearchLogTransactions transactions) {
        this.searchLogTransactions = transactions;
    }

    @Test

    public void testAddUpdateSearchLog() throws Exception {
        searchLogTransactions.addUpdateSearchLog("test_test_foo1");
        searchLogTransactions.addUpdateSearchLog("test_test_foo1");
    }

    @Test
    @Rollback(false)
    public void testAddSearchLog() throws Exception {
        searchLogTransactions.deleteSearchLog("test_test_foo2");
        searchLogTransactions.addUpdateSearchLog("test_test_foo2");
        searchLogTransactions.addUpdateSearchLog("test_test_foo2");
        searchLogTransactions.addUpdateSearchLog("test_test_foo2");
        List<JpaSearchLog> r2 = searchLogTransactions.readSearchLog("test_test_foo2");
        assertNotNull(r2);
        assertTrue(!r2.isEmpty());
        assertTrue(r2.size() == 1);
        assertEquals(3, r2.get(0).getSearchCount());

        searchLogTransactions.deleteSearchLog("test_test_foo2");

    }


}
