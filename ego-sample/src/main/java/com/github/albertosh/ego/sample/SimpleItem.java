package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;

public class SimpleItem extends EgoObject {

    private int someInt;

    public int getSomeInt() {
        return someInt;
    }

    public SimpleItem(EgoObjectBuilder builder) {
        super(builder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleItem that = (SimpleItem) o;

        return someInt == that.someInt;

    }

    @Override
    public int hashCode() {
        return someInt;
    }

    @Override
    public String toString() {
        return "SimpleItem{" +
                "someInt=" + someInt +
                ", " + super.toString() +
                '}';
    }
}
