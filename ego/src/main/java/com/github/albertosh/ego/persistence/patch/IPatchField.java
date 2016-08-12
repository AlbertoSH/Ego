package com.github.albertosh.ego.persistence.patch;

import com.github.albertosh.ego.EgoObject;

/**
 * Class used for Patch objects
 *
 * @param <T> Class of element being patched e.g.: HTCalendarStack or HTTask
 */
public interface IPatchField<T extends EgoObject> {

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
