package com.github.albertosh.ego.generator.delete;

import com.github.albertosh.ego.generator.EgoPersistenceClassGenerator;
import com.github.albertosh.ego.persistence.create.EgoCreate;
import com.github.albertosh.ego.persistence.delete.EgoDelete;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.delete.DeleteGenerator.DELETE_SUFFIX;

class DeleteImplementationGenerator extends EgoPersistenceClassGenerator {

    protected DeleteImplementationGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return DELETE_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return EgoDelete.class;
    }
}
