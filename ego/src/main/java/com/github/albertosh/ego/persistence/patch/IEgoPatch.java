package com.github.albertosh.ego.persistence.patch;

import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.persistence.filter.Filter;

public interface IEgoPatch<T extends EgoObject> {

    public <F extends Filter<T>, P extends Patch<T>> long patch(F filter, P patch);

}
