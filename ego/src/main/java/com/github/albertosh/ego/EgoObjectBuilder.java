package com.github.albertosh.ego;

import com.github.albertosh.ego.builder.Builder;


public abstract class EgoObjectBuilder<T extends EgoObject> implements Builder<T> {

    private String id;

    public String getId() {
        return id;
    }

    public EgoObjectBuilder<T> id(String id) {
        this.id = id;
        return this;
    }


    public EgoObjectBuilder<T> fromPrototype(EgoObject prototype) {
        this.id = prototype.getId();
        return this;
    }

}
