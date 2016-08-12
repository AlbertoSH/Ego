package com.github.albertosh.ego.generator.patch;

import com.github.albertosh.ego.generator.EgoInterfaceGenerator;
import com.github.albertosh.ego.persistence.patch.IEgoPatch;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.patch.PatchGenerator.PATCH_SUFFIX;

class PatchInterfaceGenerator extends EgoInterfaceGenerator {

    protected PatchInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return PATCH_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return IEgoPatch.class;
    }

}
