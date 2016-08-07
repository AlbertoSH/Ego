package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.persistence.filter.Filter;
import com.github.albertosh.ego.persistence.filter.FilterOperation;
import com.github.albertosh.ego.sample.builder.SimpleItemEgoBuilder;
import com.github.albertosh.ego.sample.codecs.SimpleItemEgoCodec;
import com.github.albertosh.ego.sample.create.ISimpleItemEgoCreate;
import com.github.albertosh.ego.sample.create.SimpleItemEgoCreate;
import com.github.albertosh.ego.sample.delete.ISimpleItemEgoDelete;
import com.github.albertosh.ego.sample.delete.SimpleItemEgoDelete;
import com.github.albertosh.ego.sample.filterfield.SimpleItemEgoFilterField;
import com.github.albertosh.ego.sample.read.ISimpleItemEgoRead;
import com.github.albertosh.ego.sample.read.SimpleItemEgoRead;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

@SuppressWarnings("unchecked")
public class SimpleItemTest {

    private final static String DB_NAME = "ego";

    private MongoClient client;

    @Before
    public void init() {
        List<Codec<?>> codecs = new ArrayList<>();
        codecs.add(new SimpleItemEgoCodec());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(codecs));

        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        client = new MongoClient("localhost:27017", options);
        // Sanity check
        client.dropDatabase("ego");
    }

    @After
    public void clean() {
        client.dropDatabase("ego");
        client.close();
        client = null;
    }

    @Test
    public void storeAndRecover() throws Exception {
        ISimpleItemEgoCreate create = new SimpleItemEgoCreate(client, DB_NAME);

        SimpleItemEgoBuilder itemBuilder = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someString("foo")
                .someInteger(2)
                .someFloat(4.3f)
                .someCharacter('p')
                .id(new ObjectId().toString());
        SimpleItem item = itemBuilder.build();

        SimpleItem inserted = create.create(itemBuilder);

        assertThat(inserted, is(equalTo(inserted)));


        ISimpleItemEgoRead read = new SimpleItemEgoRead(client, "ego");

        List<SimpleItem> recoveredList = read.read();

        assertThat(recoveredList, hasSize(1));
        SimpleItem recovered = recoveredList.get(0);
        assertThat(item, is(equalTo(recovered)));
    }

    @Test
    public void recoverFiltered() throws Exception {
        ISimpleItemEgoCreate create = new SimpleItemEgoCreate(client, DB_NAME);

        SimpleItemEgoBuilder itemBuilder1 = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someInteger(1)
                .someFloat(5f)
                .someString("asdf")
                .id(new ObjectId().toString());
        SimpleItem item1 = itemBuilder1
                .build();
        create.create(itemBuilder1);

        SimpleItemEgoBuilder itemBuilder2 = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someInteger(2)
                .someFloat(10f)
                .someString("qwerty")
                .id(new ObjectId().toString());
        SimpleItem item2 = itemBuilder2
                .build();
        create.create(itemBuilder2);

        SimpleItemEgoBuilder itemBuilder3 = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someInteger(3)
                .someFloat(50f)
                .someString("foobar")
                .id(new ObjectId().toString());
        SimpleItem item3 = itemBuilder3
                .build();
        create.create(itemBuilder3);

        ISimpleItemEgoRead read = new SimpleItemEgoRead(client, "ego");


        Filter<SimpleItem> filter = new Filter(SimpleItemEgoFilterField.SOME_INTEGER, 2);
        List<SimpleItem> result = read.read(filter);
        assertThat(result, hasSize(1));
        SimpleItem recovered = result.get(0);
        assertThat(item2, is(equalTo(recovered)));


        filter = new Filter(SimpleItemEgoFilterField.SOME_STRING, "asdf")
                .or(SimpleItemEgoFilterField.SOME_INTEGER, FilterOperation.GT, 2);
        result = read.read(filter);
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(item1, item3));
    }


    @Test
    public void delete() throws Exception {
        ISimpleItemEgoCreate create = new SimpleItemEgoCreate(client, DB_NAME);

        String item1Id = new ObjectId().toString();
        SimpleItemEgoBuilder itemBuilder1 = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someInteger(1)
                .someFloat(5f)
                .someString("asdf")
                .id(item1Id);
        SimpleItem item1 = itemBuilder1
                .build();
        create.create(itemBuilder1);

        String item2Id = new ObjectId().toString();
        SimpleItemEgoBuilder itemBuilder2 = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someInteger(2)
                .someFloat(10f)
                .someString("qwerty")
                .id(item2Id);
        SimpleItem item2 = itemBuilder2
                .build();
        create.create(itemBuilder2);

        String item3Id = new ObjectId().toString();
        SimpleItemEgoBuilder itemBuilder3 = (SimpleItemEgoBuilder) new SimpleItemEgoBuilder()
                .someInteger(3)
                .someFloat(50f)
                .someString("foobar")
                .id(item3Id);
        SimpleItem item3 = itemBuilder3
                .build();
        create.create(itemBuilder3);

        ISimpleItemEgoRead read = new SimpleItemEgoRead(client, "ego");
        List<SimpleItem> stored = read.read();
        assertThat(stored, hasSize(3));

        ISimpleItemEgoDelete delete = new SimpleItemEgoDelete(client, "ego");

        Optional<SimpleItem> deleted = delete.delete(item1Id);
        assertThat(deleted.isPresent(), is(true));
        assertThat(deleted.get(), is(equalTo(item1)));
        stored = read.read();
        assertThat(stored, hasSize(2));
        assertThat(stored, containsInAnyOrder(item2, item3));

        Filter filter = new Filter(SimpleItemEgoFilterField.SOME_STRING, "foobar");
        long deletedNumber = delete.delete(filter);
        assertThat(deletedNumber, is(1L));
        stored = read.read();
        assertThat(stored, hasSize(1));
        assertThat(stored, containsInAnyOrder(item2));
    }

}