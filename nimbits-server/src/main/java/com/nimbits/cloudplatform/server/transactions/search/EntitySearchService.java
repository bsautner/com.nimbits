package com.nimbits.cloudplatform.server.transactions.search;

import com.google.appengine.api.search.*;
import com.nimbits.cloudplatform.client.enums.EntityType;
import com.nimbits.cloudplatform.client.enums.Parameters;
import com.nimbits.cloudplatform.client.model.entity.Entity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("searchService")
public class EntitySearchService {
    protected static Index getIndex() {
        IndexSpec indexSpec = IndexSpec.newBuilder().setName("entity_index_00").build();
        return SearchServiceFactory.getSearchService().getIndex(indexSpec);
    }
    public static void updateIndex(final List<Entity> entity) {
//        Document document = Document.newBuilder()
//                .setId(entity.getUUID())
//                .addField(Field.newBuilder().setName(Parameters.name.name()).setText(entity.getName().getValue()))
//                .addField(Field.newBuilder().setName(Parameters.owner.name()).setText(entity.getOwner()))
//                .addField(Field.newBuilder().setName(Parameters.description.name()).setText(entity.getDescription()))
//                .addField(Field.newBuilder().setName(Parameters.uuid.name()).setText(entity.getUUID()))
//                .addField(Field.newBuilder().setName(Parameters.type.name()).setNumber(entity.getEntityType().getCode()))
//                .addField(Field.newBuilder().setName(Parameters.lastUpdate.name()).setDate(new Date()))
//                .addField(Field.newBuilder().setName(Parameters.protection.name()).setText(String.valueOf(entity.getProtectionLevel().getCode())))
//                .build();
//        getIndex().put(document);
    }

    public static void deleteAll() {
        while (true) {
            List<String> docIds = new ArrayList<String>();
            // Return a set of document IDs.
            GetRequest request = GetRequest.newBuilder().setReturningIdsOnly(true).build();
            GetResponse<Document> response = getIndex().getRange(request);
            if (response.getResults().isEmpty()) {
                break;
            }
            for (Document doc : response) {
                docIds.add(doc.getId());
            }
            getIndex().delete(docIds);
        }
    }

    public static Results<ScoredDocument> findEntity(final String name, final EntityType type) {

        SortOptions sortOptions = SortOptions.newBuilder()
                .addSortExpression(SortExpression.newBuilder()
                        .setExpression(Parameters.lastUpdate.name())
                        .setDirection(SortExpression.SortDirection.DESCENDING)
                        .setDefaultValue(""))
                .setLimit(100)
                .build();

        String queryString = Parameters.type.name() + "=" + type.getCode() + " AND (" + Parameters.name.name() + ":" + name  + " OR "  + Parameters.description.name() + ":" + name + ")";

        QueryOptions options = QueryOptions.newBuilder()
                .setLimit(100)
               // .setFieldsToSnippet(Parameters.description.name())
               // .setFieldsToReturn(Parameters.name.name(), Parameters.uuid.name(), Parameters.type.name())
                .setSortOptions(sortOptions)
                .build();
        Query query = Query.newBuilder().setOptions(options).build(queryString);
        Results<ScoredDocument> results = getIndex().search(query);
        return results;
    }

}
