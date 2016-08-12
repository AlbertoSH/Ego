package com.github.albertosh.ego.generator.read;

import com.github.albertosh.ego.generator.EgoInterfaceGenerator;
import com.github.albertosh.ego.persistence.read.IEgoRead;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.read.ReadGenerator.READ_SUFFIX;

class ReadInterfaceGenerator extends EgoInterfaceGenerator {

    protected ReadInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return READ_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return IEgoRead.class;
    }
}
