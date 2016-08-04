package com.github.albertosh.ego;

@Ego
public abstract class EgoObject {

    private final String id;

    public EgoObject(EgoObjectBuilder builder) {
        this.id = builder.getId();
    }

    public final String getId() {
        return id;
    }

}
