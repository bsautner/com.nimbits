package com.nimbits.server.service.form.impl;

import com.nimbits.server.transactions.dao.form.FormRequestDao;

import javax.annotation.Resource;

/**
 * User: benjamin
 * Date: 6/28/12
 * Time: 12:58 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
public class FormServiceImpl {

    @Resource(name="formDao")
    private FormRequestDao formDao;


    void addFormRequest(String name, String contact, String desc) {



    }

}
