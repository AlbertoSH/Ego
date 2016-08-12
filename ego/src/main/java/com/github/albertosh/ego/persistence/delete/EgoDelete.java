package com.github.albertosh.ego.persistence.delete;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.persistence.filter.Filter;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import org.bson.types.ObjectId;

import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public abstract class EgoDelete<T extends EgoObject>
        implements IEgoDelete<T> {

    protected final MongoClient client;
    protected final String dbName;

    public EgoDelete(MongoClient client, String dbName) {
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
    public final Optional<T> delete(String id) {
        MongoCollection<T> collection = getCollection();

        T item = collection
                .findOneAndDelete(eq("_id", new ObjectId(id)));

        return Optional.ofNullable(item);
    }

    @Override
    public final long delete(Filter<T> filter) {
        MongoCollection<T> collection = getCollection();

        DeleteResult result = collection
                .deleteMany(filter.getBsonFilter());

        return result.getDeletedCount();
    }
}
