package com.github.albertosh.ego.sample;

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
        MongoClient client = new MongoClient("localhost:27017", options);

        SimpleItem item = new SimpleItem();
        item.setB((byte) 1);
        item.setBool(true);
        item.setI(3);

        client
                .getDatabase("ego")
                .getCollection("item")
                .withDocumentClass(SimpleItem.class)
                .insertOne(item);

        SimpleItem recovered = client
                .getDatabase("ego")
                .getCollection("item")
                .withDocumentClass(SimpleItem.class)
                .find()
                .first();

        assertThat(item, is(equalTo(recovered)));

    }
}
