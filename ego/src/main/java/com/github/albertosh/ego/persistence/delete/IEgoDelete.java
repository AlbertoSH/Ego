package com.github.albertosh.ego.persistence.delete;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.persistence.filter.Filter;

import java.util.Optional;

public interface IEgoDelete<T extends EgoObject> {

    public Optional<T> delete(String id);

    public long delete(Filter<T> filter);
}
