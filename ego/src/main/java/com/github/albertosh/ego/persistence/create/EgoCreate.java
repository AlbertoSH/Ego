package com.github.albertosh.ego.persistence.create;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public abstract class EgoCreate<T extends EgoObject, B extends EgoObjectBuilder<T>>
        implements IEgoCreate<T, B> {

    protected final MongoClient client;
    protected final String dbName;

    public EgoCreate(MongoClient client, String dbName) {
        this.client = client;
        this.dbName = dbName;
    }

    private final MongoCollection<T> getCollection() {
        MongoDatabase database = client.getDatabase(dbName);

        return database
                .getCollection(getCollectionName())
                .withDocumentClass(getItemsClass());
    }

    protected abstract Class<T> getItemsClass();

    protected abstract String getCollectionName();

    @Override
    public T create(B builder) {
        MongoCollection<T> collection = getCollection();

        if (builder.getId() == null)
            builder.id(new ObjectId().toString());

        T item = builder.build();
        collection.insertOne(item);

        return item;
    }
}
