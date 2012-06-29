package com.nimbits.server.transactions.dao.form;

/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 12:57 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
public interface FormRequestDao {
    void addFormRequest(String name, String contact, String desc);
}
