package com.github.albertosh.ego.persistence.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class LocalDateTimeCodec implements Codec<LocalDateTime> {

    @Override
    public LocalDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        Date date = new Date(reader.readDateTime());
        Instant instant = date.toInstant();
        return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

    @Override
    public void encode(BsonWriter writer, LocalDateTime value, EncoderContext encoderContext) {
        writer.writeDateTime(value.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli());
    }

    @Override
    public Class<LocalDateTime> getEncoderClass() {
        return null;
    }
}
