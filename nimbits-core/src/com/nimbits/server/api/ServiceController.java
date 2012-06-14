package com.nimbits.server.api;

import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.Action;
import com.nimbits.client.enums.EntityType;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityModel;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import com.nimbits.server.transactions.dao.search.SearchLogTransactions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * User: benjamin
 * Date: 5/29/12
 * Time: 12:29 PM
 * Copyright 2012 Tonic Solutions LLC - All Rights Reserved
 */
@Transactional
@Controller
public class ServiceController {


    @Resource(name="searchDao")
    private SearchLogTransactions searchDao;

    @Resource(name="entityDao")
    private EntityJPATransactions entityDao;



    @RequestMapping(value="service/search", method= RequestMethod.GET)
    public String search(
            @RequestParam("search") String dangerousSearchText,
            @RequestParam("format") String format
            , ModelMap model) {


        final String safeSearch = safeSearchText(dangerousSearchText);
        try {
            searchDao.addUpdateSearchLog(dangerousSearchText);

            final List<Entity> result = entityDao.searchEntity(safeSearch);

            StringBuilder sb = new StringBuilder();
            if (!Utils.isEmptyString(format) && format.equals(ExportType.json.getCode())) {
                sb.append(GsonFactory.getInstance().toJson(result, GsonFactory.entityListType));
            } else {

                if (result.isEmpty()) {
                    sb.append("<p>No results</p>");
                }
                else {
                    for (Entity d : result) {
                        String img;

                        if (d.getEntityType().equals(EntityType.category)) {
                            img = "<img src=\"http://www.nimbits.com/images/folder.png\" width=30 height=30>";

                        } else {
                            img = "<img align=left src=\"http://www.nimbits.com/images/ball.png\" width=30 height=30>";
                        }
                        sb.append("<div class=\"row\">")
                                .append("<h5>").append("<a href=\"").append(d.getInstanceUrl()).append("/report.html?uuid=").append(d.getKey())
                                .append("\" target=\"_blank\">").append(d.getName()).append("</a></h5>")
                                .append(img)
                                .append("<p>").append(d.getDescription()).append("</p>")
                                .append("</div>");
                        sb.append("<div class=\"row\"></div>");



                    }
                }

                model.addAttribute("TEXT",sb.toString());




            }
        } catch (NimbitsException e) {

        }
        return "searchResults";
    }

    @RequestMapping(value="service/entity", method= RequestMethod.POST)
    public void processRequest(
            @RequestParam("entity") String json,
            @RequestParam("action") String actionParam,
            @RequestParam("instance") String instanceURL
     ) throws IOException, NimbitsException {


        if (StringUtils.isNotEmpty(json) && StringUtils.isNotEmpty(actionParam) && StringUtils.isNotEmpty(instanceURL)) {

            Entity entity = GsonFactory.getInstance().fromJson(json, EntityModel.class);
            Action action = Action.get(actionParam);
            if (action.equals(Action.update)) {
                entityDao.addUpdateEntity(entity, instanceURL);

            }
            else if (action.equals(Action.delete)) {
                entityDao.deleteEntityByUUID(entity.getUUID());
            }


        }

    }




    //todo make safer
    private String safeSearchText(final String search) {
        if (search.length() > 0
                && search.length() < 200
                && !search.contains(";")
                && !search.contains("--")
                && !search.contains("(")
                && !search.contains(")")
                && !search.contains("{")
                && !search.contains("}")) {
            return search;
        } else {
            return "nimbits data";
        }


    }

}
