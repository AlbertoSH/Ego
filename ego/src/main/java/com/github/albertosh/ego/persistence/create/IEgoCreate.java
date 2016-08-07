package com.github.albertosh.ego.persistence.create;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;

public interface IEgoCreate<T extends EgoObject, B extends EgoObjectBuilder<T>> {

    public T create(B builder);

}
