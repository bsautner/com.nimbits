package com.nimbits.cloudplatform.server.transaction;

import com.google.appengine.api.search.*;

import java.util.Date;


public class SearchServiceImpl {


    public void search() {
        Document doc = Document.newBuilder()
                .addField(Field.newBuilder().setName("content").setText("BAR"))
                .setId("FOO")
//                .addField(Field.newBuilder().setName("email")
//                        .setText(currentUser.getEmail()))
                 .addField(Field.newBuilder().setName("domain").setText("domain"))
                .addField(Field.newBuilder().setName("published").setDate(new Date()))
                .build();
        getIndex().put(doc);

        Results<ScoredDocument> results = getIndex().search("BAR");
        for (ScoredDocument s : results) {
            System.out.print(s.getOnlyField("content").getText());

        }

    }
    public Index getIndex() {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("myindex").build();
        return SearchServiceFactory.getSearchService().getIndex(indexSpec);
    }

}
