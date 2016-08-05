package com.github.albertosh.ego.generator;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.function.Function;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public abstract class EgoGenerator {

    private final Messager messager;
    private final Filer filer;

    protected TypeSpec.Builder currentTypeSpec;
    protected TypeElement currentClassElement;
    protected TypeName currentClassTypeName;
    protected String currentPackage;

    protected EgoGenerator(Messager messager, Filer filer) {
        this.messager = messager;
        this.filer = filer;
    }

    public final void generate(TypeElement classElement) {
        currentClassElement = classElement;
        currentTypeSpec = getTypeSpecBuilder().apply(getPrefix() + classElement.getSimpleName() + getSuffix());
        currentClassTypeName = TypeName.get(currentClassElement.asType());
        currentPackage = ((PackageElement) currentClassElement.getEnclosingElement()).getQualifiedName().toString();

        setClassHeader();
        doGenerate(classElement);

        currentPackage = null;
        currentClassTypeName = null;
        currentTypeSpec = null;
        currentClassElement = null;
    }

    private void setClassHeader() {
        currentTypeSpec.addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSpec.builder(Generated.class)
                                .addMember("value", "\"Ego\"")
                                .build());
    }

    protected abstract Function<String, TypeSpec.Builder> getTypeSpecBuilder();

    protected abstract void doGenerate(TypeElement classElement);

    protected abstract String getPrefix();

    protected abstract String getSuffix();

    protected String getPackageSuffix() {
        return "." + getSuffix().toLowerCase();
    };

    protected void warning(String message, Element e) {
        messager.printMessage(
                Diagnostic.Kind.WARNING,
                message,
                e);
    }

    protected void error(String message, Element e) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                message,
                e);
    }

    protected final void writeToFile() {
        JavaFile javaFile = JavaFile.builder(currentPackage + getPackageSuffix(), currentTypeSpec.build()).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
