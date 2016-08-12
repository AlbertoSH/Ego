package com.github.albertosh.ego.generator.read;

import com.github.albertosh.ego.generator.EgoPersistenceClassGenerator;
import com.github.albertosh.ego.persistence.read.EgoRead;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.read.ReadGenerator.READ_SUFFIX;

class ReadImplementationGenerator extends EgoPersistenceClassGenerator {

    protected ReadImplementationGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return READ_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return EgoRead.class;
    }
}
