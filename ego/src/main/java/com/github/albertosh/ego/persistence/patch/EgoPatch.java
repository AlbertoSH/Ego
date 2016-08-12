package com.github.albertosh.ego.persistence.patch;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.persistence.filter.Filter;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public abstract class EgoPatch<T extends EgoObject>
        implements IEgoPatch<T> {

    protected final MongoClient client;
    protected final String dbName;

    public EgoPatch(MongoClient client, String dbName) {
        this.client = client;
        this.dbName = dbName;
    }

    private MongoCollection<T> getCollection() {
        MongoDatabase database = client.getDatabase(dbName);

        return database
                .getCollection(getCollectionName())
                .withDocumentClass(getItemsClass());
    }

    protected abstract Class<T> getItemsClass();

    protected abstract String getCollectionName();

    @Override
    public <F extends Filter<T>, P extends Patch<T>> long patch(F filter, P patch) {
        MongoCollection<T> collection = getCollection();

        UpdateResult result = collection
                .updateMany(filter.getBsonFilter(), patch.getBsonUpdate());

        return result.getModifiedCount();
    }

}
