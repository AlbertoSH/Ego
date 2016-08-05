package com.github.albertosh.ego.generator.read;

import com.github.albertosh.ego.generator.EgoClassGenerator;
import com.github.albertosh.ego.generator.EgoInterfaceGenerator;
import com.github.albertosh.ego.persistence.read.EgoRead;
import com.github.albertosh.ego.persistence.read.IEgoRead;
import com.mongodb.MongoClient;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import org.bson.BsonWriter;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.github.albertosh.ego.generator.read.ReadInterfaceGenerator.READ_SUFFIX;

class ReadImplementationGenerator extends EgoClassGenerator {

    protected ReadImplementationGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return READ_SUFFIX;
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        setClassHeader();

        addConstructor();
        addGetItemClass();
        addGetCollectionName();

        writeToFile();
    }

    private void setClassHeader() {
        currentTypeSpec.addSuperinterface(ClassName.get(
                currentPackage
                + getPackageSuffix(),
                "I" + currentClassElement.getSimpleName().toString() + getSuffix()
        ));

        ParameterizedTypeName superType = ParameterizedTypeName.get(ClassName.get(EgoRead.class), currentClassTypeName);
        currentTypeSpec.superclass(superType);
    }

    private void addConstructor() {
        MethodSpec getEncoderClassMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(MongoClient.class), "client")
                .addParameter(TypeName.get(String.class), "dbName")
                .addStatement("super(client, dbName)", currentClassTypeName)
                .build();

        currentTypeSpec.addMethod(getEncoderClassMethod);
    }

    private void addGetItemClass() {
        ParameterizedTypeName classType = ParameterizedTypeName.get(ClassName.get(Class.class), currentClassTypeName);

        MethodSpec getItemClassMethod = MethodSpec.methodBuilder("getItemsClass")
                .addModifiers(Modifier.PUBLIC)
                .returns(classType)
                .addAnnotation(Override.class)
                .addStatement("return $T.class", currentClassTypeName)
                .build();

        currentTypeSpec.addMethod(getItemClassMethod);
    }

    private void addGetCollectionName() {
        MethodSpec getCollectionNameMethod = MethodSpec.methodBuilder("getCollectionName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addAnnotation(Override.class)
                .addStatement("return $S", currentClassElement.getSimpleName().toString())
                .build();

        currentTypeSpec.addMethod(getCollectionNameMethod);
    }
}
