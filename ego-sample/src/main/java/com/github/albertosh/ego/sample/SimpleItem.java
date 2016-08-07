package com.github.albertosh.ego.sample;

import com.github.albertosh.ego.EgoIgnore;
import com.github.albertosh.ego.EgoObject;

public class SimpleItem extends EgoObject {

    private String someString;
    private Integer someInteger;
    private Long someLong;
    @EgoIgnore
    private Character someCharacter;
    @EgoIgnore
    private Byte someByte;
    private Double someDouble;
    private Float someFloat;

    public String getSomeString() {
        return someString;
    }

    public Integer getSomeInteger() {
        return someInteger;
    }

    public Long getSomeLong() {
        return someLong;
    }

    public Character getSomeCharacter() {
        return someCharacter;
    }

    public Byte getSomeByte() {
        return someByte;
    }

    public Double getSomeDouble() {
        return someDouble;
    }

    public Float getSomeFloat() {
        return someFloat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleItem that = (SimpleItem) o;

        if (someString != null ? !someString.equals(that.someString) : that.someString != null)
            return false;
        if (someInteger != null ? !someInteger.equals(that.someInteger) : that.someInteger != null)
            return false;
        if (someLong != null ? !someLong.equals(that.someLong) : that.someLong != null)
            return false;
        if (someCharacter != null ? !someCharacter.equals(that.someCharacter) : that.someCharacter != null)
            return false;
        if (someByte != null ? !someByte.equals(that.someByte) : that.someByte != null)
            return false;
        if (someDouble != null ? !someDouble.equals(that.someDouble) : that.someDouble != null)
            return false;
        return someFloat != null ? someFloat.equals(that.someFloat) : that.someFloat == null;

    }

    @Override
    public int hashCode() {
        int result = someString != null ? someString.hashCode() : 0;
        result = 31 * result + (someInteger != null ? someInteger.hashCode() : 0);
        result = 31 * result + (someLong != null ? someLong.hashCode() : 0);
        result = 31 * result + (someCharacter != null ? someCharacter.hashCode() : 0);
        result = 31 * result + (someByte != null ? someByte.hashCode() : 0);
        result = 31 * result + (someDouble != null ? someDouble.hashCode() : 0);
        result = 31 * result + (someFloat != null ? someFloat.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleItem{" +
                "someString='" + someString + '\'' +
                ", someInteger=" + someInteger +
                ", someLong=" + someLong +
                ", someCharacter=" + someCharacter +
                ", someByte=" + someByte +
                ", someDouble=" + someDouble +
                ", someFloat=" + someFloat +
                '}';
    }
}
