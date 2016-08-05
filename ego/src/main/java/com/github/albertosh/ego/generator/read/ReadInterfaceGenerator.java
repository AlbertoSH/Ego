package com.github.albertosh.ego.generator.read;

import com.github.albertosh.ego.generator.EgoGenerator;
import com.github.albertosh.ego.generator.EgoInterfaceGenerator;
import com.github.albertosh.ego.persistence.read.IEgoRead;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import org.bson.codecs.Codec;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

class ReadInterfaceGenerator extends EgoInterfaceGenerator {

    public final static String READ_SUFFIX = "EgoRead";

    protected ReadInterfaceGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getPrefix() {
        return "I";
    }

    @Override
    protected String getSuffix() {
        return READ_SUFFIX;
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        setClassHeader();

        writeToFile();
    }

    private void setClassHeader() {
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(ClassName.get(IEgoRead.class), currentClassTypeName);

        currentTypeSpec.addSuperinterface(interfaceType);
    }


}
