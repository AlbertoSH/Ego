package com.github.albertosh.ego.generator.create;

import com.github.albertosh.ego.generator.EgoInterfaceGenerator;
import com.github.albertosh.ego.generator.builder.BuilderGenerator;
import com.github.albertosh.ego.persistence.create.IEgoCreate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

import static com.github.albertosh.ego.generator.create.CreateGenerator.CREATE_SUFFIX;

class CreateInterfaceGenerator extends EgoInterfaceGenerator {

    protected CreateInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return CREATE_SUFFIX;
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        setClassHeader();

        writeToFile();
    }

    private void setClassHeader() {
        ClassName currentBuilderTypeName = ClassName.get(currentPackage + ".builder", currentClassElement.getSimpleName() + "Ego" + BuilderGenerator.BUILDER_CLASS_SUFIX);
        ParameterizedTypeName currentBuilderParameterizedTypeName = ParameterizedTypeName.get(currentBuilderTypeName, currentClassTypeName);
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(ClassName.get(IEgoCreate.class), currentClassTypeName, currentBuilderParameterizedTypeName);

        currentTypeSpec.addSuperinterface(interfaceType);
    }


}
