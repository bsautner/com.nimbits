package com.nimbits.server.api;

import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.service.entity.EntityService;
import com.nimbits.server.service.search.SearchService;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.aopalliance.aop.Advice;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * User: benjamin
 * Date: 5/29/12
 * Time: 12:29 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */

@Controller
public class ServiceController {

    @Resource(name="entityService")
    private EntityService entityService;

    @Resource(name="searchService")
    private SearchService searchService;

    @RequestMapping(value="service/search", method= RequestMethod.GET)
    public String search(
            @RequestParam("search") String dangerousSearchText,
            @RequestParam("format") String format
            , ModelMap model) {

        model.addAttribute("TEXT",searchService.processSearch(dangerousSearchText, format));

        return "searchResults";
    }

    @RequestMapping(value="service/entity", method= RequestMethod.POST)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void processRequest(
            @RequestParam("entity") String json,
            @RequestParam("action") String actionParam,
            @RequestParam("instance") String instanceURL
     ) throws IOException, NimbitsException {

        entityService.processEntity(json, actionParam,  instanceURL);

    }






}
