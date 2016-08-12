package com.github.albertosh.ego.persistence.create;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;

import java.util.Optional;

public interface IEgoCreate<T extends EgoObject, B extends EgoObjectBuilder<T>> {

    public Optional<T> create(B builder);

}
