package com.github.albertosh.ego.generator;

import com.mongodb.MongoClient;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public abstract class EgoPersistenceClassGenerator extends EgoClassGenerator {

    protected EgoPersistenceClassGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        setClassHeader();

        addConstructor();
        addGetItemClass();
        addGetCollectionName();

        writeToFile();
    }

    protected void setClassHeader() {
        currentTypeSpec.addSuperinterface(ClassName.get(
                currentPackage
                        + getPackageSuffix(),
                "I" + currentClassElement.getSimpleName().toString() + "Ego" + getSuffix()
        ));

        ParameterizedTypeName superType = ParameterizedTypeName.get(ClassName.get(getPersistenceSuperClass()), currentClassTypeName);
        currentTypeSpec.superclass(superType);
    }

    protected abstract Class<?> getPersistenceSuperClass();

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
