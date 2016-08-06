package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoObjectBuilder;
import com.github.albertosh.ego.sample.codecs.SimpleItemEgoCodec;
import com.github.albertosh.ego.sample.egocreate.ISimpleItemEgoCreate;
import com.github.albertosh.ego.sample.egocreate.SimpleItemEgoCreate;
import com.github.albertosh.ego.sample.egoread.ISimpleItemEgoRead;
import com.github.albertosh.ego.sample.egoread.SimpleItemEgoRead;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.runtime.Debug.id;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;

@SuppressWarnings("unchecked")
public class SimpleItemTest {

    @Test
    public void injectedConstructor() throws Exception {
        Class<SimpleItem> klass = SimpleItem.class;
        try {
            klass.newInstance();
            throw new IllegalStateException("This was supposes to fail... :(");
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // The constructor has private access it was supposed to ;)
        }

        Constructor<SimpleItem> constructor = klass.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            throw new IllegalStateException("This was supposes to fail... :(");
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause().getClass().equals(RuntimeException.class)) {
                // The constructor failed as it was supposed to ;)
            } else {
                throw new RuntimeException(e);
            }
        }

    }

    @Test
    public void storeAndRecover() throws Exception {
        List<Codec<?>> codecs = new ArrayList<>();
        codecs.add(new SimpleItemEgoCodec());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromCodecs(codecs));

        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(codecRegistry)
                .build();
        try (MongoClient client = new MongoClient("localhost:27017", options)) {
            try {
                ISimpleItemEgoCreate create = new SimpleItemEgoCreate(client, "ego");

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
            } finally {
                client.dropDatabase("ego");
            }
        }
    }
}