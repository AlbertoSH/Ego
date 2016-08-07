package com.github.albertosh.ego.persistence.read;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.persistence.filter.Filter;

import java.util.List;
import java.util.Optional;

public interface IEgoRead<T extends EgoObject> {

    public Optional<T> read(String id);

    public List<T> read();

    public List<T> read(Filter<T> filter);
}
