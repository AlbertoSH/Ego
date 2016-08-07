package com.github.albertosh.ego.generator;

import com.squareup.javapoet.TypeSpec;

import java.util.Optional;
import java.util.function.Function;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public abstract class EgoClassGenerator extends EgoGenerator {

    protected EgoClassGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected final Function<String, TypeSpec.Builder> getTypeSpecBuilder() {
        return TypeSpec::classBuilder;
    }

    @Override
    protected final String getPrefix() {
        return "";
    }

    protected final Optional<String> methodThatReturnsValue(VariableElement field) {
        if (field.getModifiers().contains(Modifier.PUBLIC)) {
            return Optional.of(field.getSimpleName().toString());
        } else {
            String isMethodName = ("is" + field.getSimpleName().toString()).toLowerCase();
            String getMethodName = ("get" + field.getSimpleName().toString()).toLowerCase();
            Element enclosingClass = field.getEnclosingElement();
            for (Element method : enclosingClass.getEnclosedElements()) {
                if (method.getKind().equals(ElementKind.METHOD)) {
                    String methodName = method.getSimpleName().toString();
                    if (methodName.toLowerCase().equals(isMethodName) || methodName.toLowerCase().equals(getMethodName))
                        return Optional.of(methodName + "()");
                }
            }
            warning("Couldn't find a way to get " + field.getSimpleName().toString() + "!\nThis can be easily fixed with a get method.", field);
            return Optional.empty();
        }
    }
}
