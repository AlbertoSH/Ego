package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;

public class SimpleItem extends EgoObject {

    private int someInt;
    private long someLong;
    private char someChar;
    
    public int getSomeInt() {
        return someInt;
    }

    public long getSomeLong() {
        return someLong;
    }

    public char getSomeChar() {
        return someChar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleItem that = (SimpleItem) o;

        if (someInt != that.someInt) return false;
        if (someLong != that.someLong) return false;
        return someChar == that.someChar;

    }

    @Override
    public int hashCode() {
        int result = someInt;
        result = 31 * result + (int) (someLong ^ (someLong >>> 32));
        result = 31 * result + (int) someChar;
        return result;
    }

    @Override
    public String toString() {
        return "SimpleItem{" +
                "someInt=" + someInt +
                ", someLong=" + someLong +
                ", someChar=" + someChar +
                ", " + super.toString() +
                '}';
    }
}
