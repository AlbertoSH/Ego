package com.github.albertosh.ego.persistence.patch;

import com.github.albertosh.ego.persistence.filter.Filter;

import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.mongodb.client.model.Updates.addEachToSet;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.pullByFilter;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;

public enum PatchOperation {
    SET {
        @Override
        public Bson patchOperation(String field, Object value) {
            return set(field, value);
        }
    },
    UNSET {
        @Override
        public Bson patchOperation(String field, Object value) {
            return unset(field);
        }
    },
    INC {
        @Override
        public Bson patchOperation(String field, Object value) {
            if (value instanceof Integer)
                return inc(field, (Integer) value);
            else
                throw new IllegalArgumentException("You can't increment by a non integer value");
        }
    },
    DEC {
        @Override
        public Bson patchOperation(String field, Object value) {
            if (value instanceof Integer)
                return inc(field, -(int) value);
            else
                throw new IllegalArgumentException("You can't increment by a non integer value");
        }
    },
    PUSH {
        @Override
        public Bson patchOperation(String field, Object value) {
            return push(field, value);
        }
    },
    PULL {
        @Override
        public Bson patchOperation(String field, Object value) {
            return pull(field, value);
        }
    },
    PULL_FILTERED {
        @Override
        public Bson patchOperation(String field, Object value) {
            if (value instanceof Filter) {
                Filter filter = (Filter) value;
                return pullByFilter(filter.getBsonFilter());
            } else {
                throw new IllegalStateException("PULL_FILTERED patch operation needs a Filter as parameter");
            }
        }
    },
    ADD_TO_SET {
        @Override
        public Bson patchOperation(String field, Object value) {
            if (value instanceof Collection) {
                @SuppressWarnings("unchecked")
                List asList = new ArrayList((Collection) value);
                return addEachToSet(field, asList);
            } else {
                return addToSet(field, value);
            }
        }
    };

    public abstract Bson patchOperation(String field, Object value);
}