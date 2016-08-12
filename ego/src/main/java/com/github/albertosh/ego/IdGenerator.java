package com.github.albertosh.ego;

import org.bson.types.ObjectId;

public class IdGenerator {

    public String generate() {
        return new ObjectId().toString();
    }

}
