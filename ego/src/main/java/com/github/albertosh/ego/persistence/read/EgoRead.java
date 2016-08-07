package com.github.albertosh.ego.persistence.read;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.persistence.filter.Filter;
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

public abstract class EgoRead<T extends EgoObject>
        implements IEgoRead<T> {

    protected final MongoClient client;
    protected final String dbName;

    public EgoRead(MongoClient client, String dbName) {
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
    public final Optional<T> read(String id) {
        MongoCollection<T> collection = getCollection();

        FindIterable<T> iterable = collection
                .find(eq("_id", new ObjectId(id)));

        T item = iterable.first();

        return Optional.ofNullable(item);
    }

    @Override
    public final List<T> read() {
        MongoCollection<T> collection = getCollection();

        FindIterable<T> iterable = collection.find();

        List<T> result = new ArrayList<>();
        iterable.forEach((Block<T>) result::add);
        return result;
    }

    @Override
    public final List<T> read(Filter<T> filter) {
        MongoCollection<T> collection = getCollection();

        FindIterable<T> iterable = collection.find(filter.getBsonFilter());

        List<T> result = new ArrayList<>();
        iterable.forEach((Block<T>) result::add);
        return result;
    }

}
