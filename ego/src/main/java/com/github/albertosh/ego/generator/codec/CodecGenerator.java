package com.github.albertosh.ego.generator.codec;


import com.github.albertosh.ego.generator.EgoClassGenerator;
import com.github.albertosh.ego.EgoIgnore;
import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;
import com.github.albertosh.ego.generator.builder.BuilderGenerator;
import com.github.albertosh.ego.persistence.codec.EgoCodec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import com.sun.tools.javac.code.Type.ClassType;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class CodecGenerator extends EgoClassGenerator {

    private final String CODEC_SUFFIX = "EgoCodec";
    private final String CODEC_PACKAGE_SUFFIX = ".codecs";

    private final Types types;
    private final Map<String, ClassName> superClassCodecMap;
    protected TypeName currentBuilderTypeName;

    public CodecGenerator(Filer filer, Messager messager, Types typeUtils) {
        super(messager, filer);
        this.types = typeUtils;
        this.superClassCodecMap = new HashMap<>();
        ClassName egoClass = ClassName.get(EgoObject.class);
        superClassCodecMap.put(egoClass.packageName() + "." + egoClass.simpleName(), ClassName.get(EgoCodec.class));
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        currentBuilderTypeName = ClassName.get(currentPackage, currentClassElement.getSimpleName() + BuilderGenerator.BUILDER_CLASS_SUFIX);

        setClassHeader();
        addGetEncoderClassMethod();
        addGetNewBuilder();
        addEncodeCurrentObject();
        addDecodeCurrentField();

        writeToFile();

        currentBuilderTypeName = null;
    }

    @Override
    protected String getSuffix() {
        return CODEC_SUFFIX;
    }

    @Override
    protected String getPackageSuffix() {
        return CODEC_PACKAGE_SUFFIX;
    }

    private void setClassHeader() {
        ParameterizedTypeName interfaceType = ParameterizedTypeName.get(ClassName.get(Codec.class), currentClassTypeName);
        currentTypeSpec.addSuperinterface(interfaceType);

        String superClassQuialifiedName = currentClassElement.getSuperclass().toString();
        ClassName codecSuperClass = getCodecClassForClass(superClassQuialifiedName);
        ParameterizedTypeName superType = ParameterizedTypeName.get(codecSuperClass, currentClassTypeName);
        currentTypeSpec.superclass(superType);
    }

    private void addGetNewBuilder() {
        MethodSpec getNewBuilderMethod = MethodSpec.methodBuilder("getNewBuilder")
                .addModifiers(Modifier.PROTECTED)
                .returns(currentBuilderTypeName)
                .addAnnotation(Override.class)
                .addStatement("return new $T()", currentBuilderTypeName)
                .build();
        currentTypeSpec.addMethod(getNewBuilderMethod);
    }

    private ClassName getCodecClassForClass(String superClassQuialifiedName) {
        return superClassCodecMap.get(superClassQuialifiedName);
    }

    private void addGetEncoderClassMethod() {
        ParameterizedTypeName classType = ParameterizedTypeName.get(ClassName.get(Class.class), currentClassTypeName);

        MethodSpec getEncoderClassMethod = MethodSpec.methodBuilder("getEncoderClass")
                .addModifiers(Modifier.PUBLIC)
                .returns(classType)
                .addAnnotation(Override.class)
                .addStatement("return $T.class", currentClassTypeName)
                .build();

        currentTypeSpec.addMethod(getEncoderClassMethod);
    }

    private void addEncodeCurrentObject() {
        MethodSpec.Builder encodeMethod = MethodSpec.methodBuilder("encodeCurrentObject")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(BsonWriter.class), "writer")
                .addParameter(currentClassTypeName, "value")
                .addParameter(TypeName.get(EncoderContext.class), "context")
                .addStatement("super.encodeCurrentObject(writer, value, context)");

        encodeFields(encodeMethod);

        currentTypeSpec.addMethod(encodeMethod.build());
    }

    private void encodeFields(MethodSpec.Builder encodeMethod) {
        for (Element e : currentClassElement.getEnclosedElements()) {
            if ((e.getKind().isField())
                    && (e.getAnnotation(EgoIgnore.class)) == null) {
                VariableElement field = (VariableElement) e;
                encodeField(encodeMethod, field);
            }
        }
    }

    private void encodeField(MethodSpec.Builder encodeMethod, VariableElement field) {
        Optional<String> method = methodThatReturnsValue(field);
        if (method.isPresent()) {
            if (field.asType() instanceof PrimitiveType) {
                encodeMethod
                        .addStatement("writer.writeName($S)", field.getSimpleName());
                writeField(encodeMethod, field, method.get());

            } else {
                encodeMethod
                        .beginControlFlow("if (value.$L != null)", method.get())
                        .addStatement("writer.writeName($S)", field.getSimpleName());
                writeField(encodeMethod, field, method.get());
                encodeMethod
                        .endControlFlow();
            }
        }
    }

    private void writeField(MethodSpec.Builder encodeMethod, VariableElement field, String getMethod) {
        if (field.asType() instanceof PrimitiveType)
            writePrimitiveField(encodeMethod, field, getMethod);
        else
            writeClassField(encodeMethod, field, getMethod);
    }

    private void writePrimitiveField(MethodSpec.Builder encodeMethod, VariableElement field, String getMethod) {
        PrimitiveType primitiveType = (PrimitiveType) field.asType();
        String writerMethod = getWriterMethodForType(primitiveType.toString());
        encodeMethod.addStatement("writer." + writerMethod + "("
                + addModifiersForEncode(primitiveType.toString())
                + ")", getMethod);
    }

    private void writeClassField(MethodSpec.Builder encodeMethod, VariableElement field, String getMethod) {
        ClassType classType = (ClassType) field.asType();
        String writerMethod = getWriterMethodForType(classType.toString());
        encodeMethod.addStatement("writer." + writerMethod + "("
                + addModifiersForEncode(classType.toString())
                + ")", getMethod);
    }

    private String addModifiersForEncode(String type) {
        switch (type) {
            case "char":
            case "java.lang.Character":
                return "String.valueOf(value.$L)";
            default:
                return "value.$L";
        }
    }

    private String getWriterMethodForType(String type) throws IllegalArgumentException {
        switch (type) {
            case "byte":
            case "java.lang.Byte":
            case "int":
            case "java.lang.Integer":
            case "short":
            case "java.lang.Short":
                return "writeInt32";
            case "long":
            case "java.lang.Long":
                return "writeInt64";
            case "boolean":
            case "java.lang.Boolean":
                return "writeBoolean";
            case "char":
            case "java.lang.Character":
            case "java.lang.String":
                return "writeString";
            case "double":
            case "java.lang.Double":
            case "float":
            case "java.lang.Float":
                return "writeDouble";
            default:
                throw new IllegalArgumentException("Unkown type: " + type);
        }
    }



    private void addDecodeCurrentField() {
        ParameterizedTypeName egoBuilderParameterized = ParameterizedTypeName.get(ClassName.get(EgoObjectBuilder.class), currentClassTypeName);
        TypeVariableName variableBuilder = TypeVariableName.get("B", egoBuilderParameterized);
        MethodSpec.Builder decodeMethod = MethodSpec.methodBuilder("decodeCurrentField")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .addTypeVariable(variableBuilder)
                .addParameter(TypeName.get(BsonReader.class), "reader")
                .addParameter(TypeName.get(DecoderContext.class), "context")
                .addParameter(variableBuilder, "builder")
                .addParameter(String.class, "field");

        decodeFields(decodeMethod);

        currentTypeSpec.addMethod(decodeMethod.build());
    }

    private void decodeFields(MethodSpec.Builder decodeMethod) {
        decodeMethod
                .addStatement("$T currentBuilder = ($T) builder", currentBuilderTypeName, currentBuilderTypeName)
                .beginControlFlow("switch (field)");

        for (Element e : currentClassElement.getEnclosedElements()) {
            if ((e.getKind().isField())
                    && (e.getAnnotation(EgoIgnore.class)) == null) {
                VariableElement field = (VariableElement) e;
                decodeField(decodeMethod, field);
            }
        }

        decodeMethod
                .addStatement("default:")
                .addStatement("super.decodeCurrentField(reader, context, builder, field)");

        decodeMethod
                .endControlFlow();
    }

    private void decodeField(MethodSpec.Builder decodeMethod, VariableElement field) {
        TypeMirror type = field.asType();
        String readerMethod = getReaderMethodForType(type.toString());
        decodeMethod
                .addStatement("case $S:", field.getSimpleName())
                .addStatement("currentBuilder.$L("
                        + addModifiersForDecode(type.toString(), readerMethod)
                        + ")", field.getSimpleName())
                .addStatement("break");
    }

        /*
                // TODO
        Optional<String> method = methodThatReturnsValue(field);
        if (method.isPresent()) {
            try {
                PrimitiveType primitiveType = (PrimitiveType) field.asType();

                encodeMethod
                        .addStatement("writer.writeName($S)", field.getSimpleName());
                writeField(encodeMethod, field, method.get());

            } catch (ClassCastException e) {
                encodeMethod
                        .beginControlFlow("if (value.$L != null)", method.get())
                        .addStatement("writer.writeName($S)", field.getSimpleName());
                writeField(encodeMethod, field, method.get());
                encodeMethod
                        .endControlFlow();
            }
        }
        */



    private String getReaderMethodForType(String type) throws IllegalArgumentException {
        switch (type) {
            case "byte":
            case "java.lang.Byte":
            case "int":
            case "java.lang.Integer":
            case "short":
            case "java.lang.Short":
                return "readInt32";
            case "long":
            case "java.lang.Long":
                return "readInt64";
            case "boolean":
            case "java.lang.Boolean":
                return "readBoolean";
            case "char":
            case "java.lang.Character":
            case "java.lang.String":
                return "readString";
            case "double":
            case "java.lang.Double":
            case "float":
            case "java.lang.Float":
                return "readDouble";
            default:
                throw new IllegalArgumentException("Unkown type: " + type);
        }
    }

    private String addModifiersForDecode(String type, String readerMethod) {
        switch (type) {
            case "char":
            case "java.lang.Character":
                return "reader." + readerMethod + "().charAt(0)";
            case "byte":
            case "float":
            case "short":
            case "java.lang.Short":
                return "(" + type + ") reader." + readerMethod + "()";
            case "java.lang.Float":
                return "((float) reader." + readerMethod + "())";
            case "java.lang.Byte":
                return "((Integer) reader." + readerMethod + "()).byteValue()";
            default:
                return "reader." + readerMethod + "()";
        }
    }

}
