package com.nimbits.server.api;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.server.service.entity.EntityService;
import com.nimbits.server.service.search.SearchService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

/**
 * User: benjamin
 * Date: 5/29/12
 * Time: 12:29 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */

@Controller
public class ServiceController {
    private static final Logger log = Logger.getLogger(ServiceController.class.getName());

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
     ){

        try {
            entityService.processEntity(json, actionParam,  instanceURL);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
            log.severe(ExceptionUtils.getStackTrace(e));
        }

    }

    @RequestMapping(value="service/location", method= RequestMethod.POST)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void receiveLocation(
            @RequestParam("entity") String json,
            @RequestParam("location") String location
    ){
        log.info("call to service location" + json + " " + location);
        try {
            entityService.processLocation(json, location);
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
            log.severe(ExceptionUtils.getStackTrace(e));
        }

    }

    @RequestMapping(value="service/location.html", method= RequestMethod.GET)
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void getEntities(ModelMap model){

            List<String[]> list = entityService.getLocations();
            StringBuilder sb = new StringBuilder();

            sb.append(" var neighborhoods = [\n");

              for (String[] s : list) {
                  String fixed = s[4].toString().replace("POINT", "").replace(" ", ", ");

                 sb.append("new google.maps.LatLng").append(fixed).append(",\n");
              }

                sb.append("        ];");
                sb.append("\n");


        sb.append(" var uuids = [\n");

        for (String[] s : list) {

             sb.append("'" + s[5] + "?uuid=" + s[0] + "'").append(",\n");
        }

        sb.append("];");
        sb.append("\n");

        sb.append(" var desc = [\n");

        for (String[] s : list) {

            sb.append("'" + s[1] + " " + s[2] + "'").append(",\n");
        }

        sb.append("];");
        sb.append("\n");

            model.addAttribute("TEXT",sb.toString());


    }


}
