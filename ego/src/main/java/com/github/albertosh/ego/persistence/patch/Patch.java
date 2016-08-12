package com.github.albertosh.ego.persistence.patch;

import com.github.albertosh.ego.EgoObject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.Optional;

import static com.github.albertosh.ego.persistence.patch.IPatchField.ID_KEY;
import static com.github.albertosh.ego.persistence.patch.PatchOperation.SET;
import static com.github.albertosh.ego.persistence.patch.PatchOperation.UNSET;
import static com.mongodb.client.model.Updates.combine;

public class Patch<T extends EgoObject> {

    private final Bson bsonUpdate;

    public <PF extends IPatchField<T>> Patch(PF field, Object value) {
        this(SET, field, value);
    }

    public <PF extends IPatchField<T>> Patch(PatchOperation op, PF field, Object value) {
        this.bsonUpdate = op.patchOperation(field.getKey(), transformToObjectIdIfNecessary(field.getKey(), value));
    }

    private Patch(Bson... updates) {
        this.bsonUpdate = combine(updates);
    }

    private Object transformToObjectIdIfNecessary(String field, Object value) {
        if ((value != null) &&
                ((field.toLowerCase().endsWith(ID_KEY))
                        || (field.toLowerCase().endsWith(ID_KEY + "s")))) {
            if (value instanceof String) {
                return new ObjectId((String) value);
            } else if (value instanceof Optional) {
                Optional opt = (Optional) value;
                if (opt.isPresent()) {
                    Object v = opt.get();
                    if (v instanceof String)
                        return new ObjectId((String) v);
                    else
                        throw new IllegalArgumentException("An ID must be an String");
                } else {
                    return null;
                }
            } else if (value instanceof ObjectId) {
                return value;
            } else if (value instanceof Collection) {
                IdsCollection ids = new IdsCollection();
                Collection<?> asCollection = (Collection) value;
                asCollection.stream()
                        .map(id -> {
                            if (id instanceof String)
                                return new ObjectId((String) id);
                            else if (id instanceof ObjectId)
                                return (ObjectId) id;
                            else
                                throw new IllegalArgumentException("String or ObjectId are required!");
                        })
                        .forEach(ids::add);
                return ids;
            }
            throw new IllegalArgumentException("An ID must be an String");
        } else {
            return value;
        }
    }

    public Patch<T> and(Patch<T> patch) {
        return new Patch<>(bsonUpdate, patch.getBsonUpdate());
    }

    public <PF extends IPatchField<T>> Patch<T> and(PF field, Object value) {
        return and(SET, field, value);
    }

    public <PF extends IPatchField<T>> Patch<T> and(PatchOperation op, PF field, Object value) {
        if ((op == SET) &&
                ((value == null))
                ||
                ((value instanceof Collection) && (((Collection) value).isEmpty()))) {
            return new Patch<>(
                    this.bsonUpdate,
                    UNSET.patchOperation(field.getKey(), null)
            );
        } else {
            return new Patch<>(
                    this.bsonUpdate,
                    op.patchOperation(field.getKey(), transformToObjectIdIfNecessary(field.getKey(), value)));
        }
    }

    public Bson getBsonUpdate() {
        return bsonUpdate;
    }

}
