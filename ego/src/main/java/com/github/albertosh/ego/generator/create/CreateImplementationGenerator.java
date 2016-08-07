package com.github.albertosh.ego.generator.create;

import com.github.albertosh.ego.generator.EgoPersistenceClassGenerator;
import com.github.albertosh.ego.generator.builder.BuilderGenerator;
import com.github.albertosh.ego.persistence.create.EgoCreate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;

import static com.github.albertosh.ego.generator.create.CreateGenerator.CREATE_SUFFIX;

class CreateImplementationGenerator extends EgoPersistenceClassGenerator {

    protected CreateImplementationGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return CREATE_SUFFIX;
    }

    @Override
    protected Class<?> getPersistenceSuperClass() {
        return EgoCreate.class;
    }

    @Override
    protected void setClassHeader() {
        currentTypeSpec.addSuperinterface(ClassName.get(
                currentPackage
                        + getPackageSuffix(),
                "I" + currentClassElement.getSimpleName().toString() + "Ego" + getSuffix()
        ));

        ClassName currentBuilderTypeName = ClassName.get(currentPackage + ".builder", currentClassElement.getSimpleName() + "Ego" + BuilderGenerator.BUILDER_CLASS_SUFIX);
        ParameterizedTypeName currentBuilderParameterizedTypeName = ParameterizedTypeName.get(currentBuilderTypeName, currentClassTypeName);
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(ClassName.get(EgoCreate.class), currentClassTypeName, currentBuilderParameterizedTypeName);

        currentTypeSpec.superclass(interfaceType);
    }
}
