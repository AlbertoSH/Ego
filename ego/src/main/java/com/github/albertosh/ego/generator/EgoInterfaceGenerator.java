package com.github.albertosh.ego.generator;

import com.squareup.javapoet.TypeSpec;

import java.util.function.Function;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

public abstract class EgoInterfaceGenerator extends EgoGenerator {

    protected EgoInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected final Function<String, TypeSpec.Builder> getTypeSpecBuilder() {
        return TypeSpec::interfaceBuilder;
    }

    @Override
    protected String getPrefix() {
        return "I";
    }
}
