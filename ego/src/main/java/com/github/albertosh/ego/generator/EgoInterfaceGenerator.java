package com.github.albertosh.ego.generator;

import com.github.albertosh.ego.persistence.read.IEgoRead;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.function.Function;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

public abstract class EgoInterfaceGenerator extends EgoGenerator {

    protected EgoInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected final Function<String, TypeSpec.Builder> getTypeSpecBuilder() {
        return TypeSpec::interfaceBuilder;
    }

    @Override
    protected final String getPrefix() {
        return "I";
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        setClassHeader();

        writeToFile();
    }

    protected void setClassHeader() {
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(ClassName.get(getPersistenceSuperClass()), currentClassTypeName);

        currentTypeSpec.addSuperinterface(interfaceType);
    }

    protected abstract Class<?> getPersistenceSuperClass();
}
