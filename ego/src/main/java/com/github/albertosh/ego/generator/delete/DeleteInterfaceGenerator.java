package com.github.albertosh.ego.generator.delete;

import com.github.albertosh.ego.generator.EgoInterfaceGenerator;
import com.github.albertosh.ego.persistence.create.IEgoCreate;
import com.github.albertosh.ego.persistence.delete.IEgoDelete;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.delete.DeleteGenerator.DELETE_SUFFIX;

class DeleteInterfaceGenerator extends EgoInterfaceGenerator {

    protected DeleteInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return DELETE_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return IEgoDelete.class;
    }

}
