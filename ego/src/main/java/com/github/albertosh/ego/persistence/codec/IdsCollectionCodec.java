package com.github.albertosh.ego.persistence.codec;

import com.github.albertosh.ego.persistence.patch.IdsCollection;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class IdsCollectionCodec implements Codec<IdsCollection> {

    @Override
    public IdsCollection decode(BsonReader reader, DecoderContext decoderContext) {
        IdsCollection collection = new IdsCollection();
        reader.readStartArray();
        collection.add(reader.readObjectId());
        reader.readEndArray();
        return collection;
    }

    @Override
    public void encode(BsonWriter writer, IdsCollection value, EncoderContext encoderContext) {
        writer.writeStartArray();
        value.stream()
                .forEach(writer::writeObjectId);
        writer.writeEndArray();
    }

    @Override
    public Class<IdsCollection> getEncoderClass() {
        return IdsCollection.class;
    }
}
