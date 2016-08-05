package com.github.albertosh.ego.persistence.create;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;

import java.util.List;
import java.util.Optional;

public interface IEgoCreate<T extends EgoObject, B extends EgoObjectBuilder<T>> {

    public T create(B builder);

}
