package com.github.albertosh.ego.codec;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

public abstract class EgoCodec<T extends EgoObject> implements Codec<T> {

    @Override
    public final T decode(BsonReader reader, DecoderContext decoderContext) {
        return decodeDocument(reader, decoderContext);
    }

    private <B extends EgoObjectBuilder<T>> T decodeDocument(BsonReader reader, DecoderContext decoderContext) {
        B builder = getNewBuilder();
        reader.readStartDocument();
        builder.id(reader.readObjectId("_id").toString());

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String field = reader.readName();
            decodeCurrentField(reader, decoderContext, builder, field);
        }

        reader.readEndDocument();
        return builder.build();
    }

    protected abstract <B extends EgoObjectBuilder<T>> B getNewBuilder();

    // To be overridden by child classes
    protected <B extends EgoObjectBuilder<T>> void decodeCurrentField(BsonReader reader,
                                                                      DecoderContext context,
                                                                      B builder,
                                                                      String field) {
    }


    @Override
    public final void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId(value.getId()));

        encodeCurrentObject(writer, value, encoderContext);

        writer.writeEndDocument();
    }

    // To be overridden by child classes
    protected void encodeCurrentObject(BsonWriter writer,
                                       T value,
                                       EncoderContext context) {
    }

}
