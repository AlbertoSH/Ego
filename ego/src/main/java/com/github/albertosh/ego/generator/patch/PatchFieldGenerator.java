package com.github.albertosh.ego.generator.patch;

import com.github.albertosh.ego.EgoIgnore;
import com.github.albertosh.ego.generator.EgoEnumGenerator;
import com.github.albertosh.ego.persistence.patch.IPatchField;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class PatchFieldGenerator extends EgoEnumGenerator {

    private final static String PATCH_FIELD_SUFFIX = "PatchField";

    public PatchFieldGenerator(Messager messager, Filer filer) {
        super(messager, filer);
    }

    @Override
    protected String getSuffix() {
        return PATCH_FIELD_SUFFIX;
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        setClassHeader();

        addFields();

        writeToFile();
    }

    private void setClassHeader() {
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(ClassName.get(IPatchField.class), currentClassTypeName);
        currentTypeSpec.addSuperinterface(interfaceType);
    }

    private void addFields() {
        for (Element e : currentClassElement.getEnclosedElements()) {
            if (e.getKind().isField() && (e.getAnnotation(EgoIgnore.class) == null)) {
                addSingleField((VariableElement) e);
            }
        }
    }

    private void addSingleField(VariableElement field) {
        StringBuilder fieldNameBuilder = new StringBuilder();

        String fieldName = field.getSimpleName().toString();
        for (int i = 0; i < fieldName.length(); i++) {
            char character = fieldName.charAt(i);
            if ((character >= 'A') && (character <= 'Z'))
                fieldNameBuilder.append("_").append(character);
            else if ((character >= 'a') && (character <= 'z'))
                fieldNameBuilder.append(Character.toUpperCase(character));
            else
                fieldNameBuilder.append(character);
        }

        currentTypeSpec.addEnumConstant(fieldNameBuilder.toString());
    }
}
