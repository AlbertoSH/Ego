package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.sample.codecs.SimpleItemEgoCodec;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello!");

        List<Codec<?>> codecs = new ArrayList<>();
        codecs.add(new SimpleItemEgoCodec());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(codecs));

        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        try (MongoClient client = new MongoClient("localhost:27017", options)) {

            SimpleItem item = (SimpleItem) new SimpleItemBuilder()
                    .someInt(2)
                    .id(new ObjectId().toString())
                    .build();

            MongoCollection<SimpleItem> collection = client
                    .getDatabase("ego")
                    .getCollection("item")
                    .withDocumentClass(SimpleItem.class);

            collection
                    .insertOne(item);

            SimpleItem recovered = collection
                    .find()
                    .first();

            collection.drop();

            assertThat(item, is(equalTo(recovered)));

            System.out.println("Success!");

        }
    }
}
