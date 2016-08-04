package com.github.albertosh.ego;

@Ego
public abstract class EgoObject {

    private final String id;

    public EgoObject(EgoObjectBuilder builder) {
        this.id = builder.getId();
    }

    public EgoObject(String id) {
        this.id = id;
    }

    public final String getId() {
        return id;
    }

}
