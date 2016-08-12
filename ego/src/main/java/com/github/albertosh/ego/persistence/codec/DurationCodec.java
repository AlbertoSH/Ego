package com.github.albertosh.ego.persistence.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Duration;

public class DurationCodec implements Codec<Duration> {

    @Override
    public Duration decode(BsonReader reader, DecoderContext decoderContext) {
        return Duration.parse(reader.readString());
    }

    @Override
    public void encode(BsonWriter writer, Duration value, EncoderContext encoderContext) {
        writer.writeString(value.toString());
    }

    @Override
    public Class<Duration> getEncoderClass() {
        return Duration.class;
    }

}
