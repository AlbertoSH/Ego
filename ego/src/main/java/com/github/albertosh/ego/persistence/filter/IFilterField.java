package com.github.albertosh.ego.persistence.filter;

import com.github.albertosh.ego.EgoObject;

public interface IFilterField<T extends EgoObject> {

    final static String ID_KEY = "id";

    default String getKey() {
        StringBuilder builder = new StringBuilder(toString().toLowerCase());

        if (builder.toString().equals(ID_KEY))
            return "_id";

        while (builder.indexOf("_") != -1) {
            int index = builder.indexOf("_");
            builder.replace(index, index + 1, "");
            builder.replace(index, index + 1, builder.substring(index, index + 1).toUpperCase());
        }

        return builder.toString();
    }

}
