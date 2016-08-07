package com.github.albertosh.ego.generator;

import com.squareup.javapoet.TypeSpec;

import java.util.function.Function;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

public abstract class EgoEnumGenerator extends EgoGenerator {

    protected EgoEnumGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected final Function<String, TypeSpec.Builder> getTypeSpecBuilder() {
        return TypeSpec::enumBuilder;
    }

    @Override
    protected final String getPrefix() {
        return "";
    }

}
