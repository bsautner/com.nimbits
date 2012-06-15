package com.nimbits.server.service.search.impl;

import com.nimbits.client.common.Utils;
import com.nimbits.client.enums.ExportType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.server.gson.GsonFactory;
import com.nimbits.server.service.search.SearchService;
import com.nimbits.server.transactions.dao.entity.EntityJPATransactions;
import com.nimbits.server.transactions.dao.search.SearchLogTransactions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Transactional
@Service("searchService")
public class SearchServiceImpl implements SearchService {
    @Resource(name="searchDao")
    private SearchLogTransactions searchDao;

    @Resource(name="entityDao")
    private EntityJPATransactions entityDao;

    @Override
    public String processSearch(String dangerousSearchText, String format) {
        final String safeSearch = safeSearchText(dangerousSearchText);
        StringBuilder sb = new StringBuilder();
        try {
            searchDao.addUpdateSearchLog(dangerousSearchText);

            final List<Entity> result = entityDao.searchEntity(safeSearch);


            if (!Utils.isEmptyString(format) && format.equals(ExportType.json.getCode())) {
                sb.append(GsonFactory.getInstance().toJson(result, GsonFactory.entityListType));
            } else {

                if (result.isEmpty()) {
                    sb.append("<p>No results</p>");
                }
                else  {
                    for (Entity d : result) {
                        String img;
//
//                        if (d.getEntityType().equals(EntityType.category)) {
//                            img = "<img src=\"http://www.nimbits.com/images/folder.png\" width=30 height=30>";
//
//                        } else {
//                            img = "<img align=left src=\"http://www.nimbits.com/images/ball.png\" width=30 height=30>";
//                        }
                        sb.append("<div class=\"row\" style=\"margin-left: 20px\">")
                                .append("<a href=\"")
                                .append(d.getInstanceUrl())
                                .append("/report.html?uuid=")
                                .append(d.getKey())
                                .append("\" target=\"_blank\" style=\"font-size: 16px; font-weight:bold \" >")
                                .append(d.getName())
                                .append("</a>")
                                .append("<p>")
                                .append(d.getDescription())
                                .append("</p>")
                                .append("</div>");
                        sb.append("<div class=\"row\"></div>");
                    }
                }

            }
        } catch (NimbitsException e) {
             return e.getMessage();
        }
        return sb.toString();
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
