package com.github.albertosh.ego;

@Ego
public abstract class EgoObject {

    private final String id;

    public EgoObject() {
        throw new RuntimeException("This constructor is not intended to be used!\n" +
                "It's here just to stop the IDE complaining about default constructor");
    }

    public EgoObject(EgoObjectBuilder builder) {
        this.id = builder.getId();
    }

    public final String getId() {
        return id;
    }

}
