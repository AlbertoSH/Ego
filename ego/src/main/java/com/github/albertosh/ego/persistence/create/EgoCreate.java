package com.github.albertosh.ego.persistence.create;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.types.ObjectId;

import java.util.Optional;

public abstract class EgoCreate<T extends EgoObject, B extends EgoObjectBuilder<T>>
        implements IEgoCreate<T, B> {

    protected final MongoClient client;
    protected final String dbName;

    public EgoCreate(MongoClient client, String dbName) {
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
    public final Optional<T> create(B builder) {
        MongoCollection<T> collection = getCollection();

        if (builder.getId() == null)
            builder.id(new ObjectId().toString());

        T item = builder.build();

        try {
            collection.insertOne(item);

            return Optional.of(item);
        } catch (MongoException e) {
            return Optional.empty();
        }
    }
}
