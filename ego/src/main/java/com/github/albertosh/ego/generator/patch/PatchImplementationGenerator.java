package com.github.albertosh.ego.generator.patch;

import com.github.albertosh.ego.generator.EgoPersistenceClassGenerator;
import com.github.albertosh.ego.persistence.patch.EgoPatch;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.patch.PatchGenerator.PATCH_SUFFIX;

class PatchImplementationGenerator extends EgoPersistenceClassGenerator {

    protected PatchImplementationGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return PATCH_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return EgoPatch.class;
    }
}
