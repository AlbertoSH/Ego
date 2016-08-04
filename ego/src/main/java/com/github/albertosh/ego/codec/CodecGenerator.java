package com.github.albertosh.ego.codec;


import com.github.albertosh.ego.EgoIgnore;
import com.github.albertosh.ego.builder.BuilderGenerator;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class CodecGenerator {

    private final String CODEC_SUFFIX = "EgoCodec";
    private final String CODEC_PACKAGE_SUFFIX = ".codecs";

    private final Filer filer;
    private final Messager messager;
    private final Types types;

    private TypeSpec.Builder currentTypeSpec;
    private TypeElement currentClassElement;
    private TypeName currentClassTypeName;
    private String currentPackage;

    public CodecGenerator(Filer filer, Messager messager, Types typeUtils) {
        this.filer = filer;
        this.messager = messager;
        this.types = typeUtils;
    }

    private void warning(String message, Element e) {
        messager.printMessage(
                Diagnostic.Kind.WARNING,
                message,
                e);
    }

    private void error(String message, Element e) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                message,
                e);
    }

    public void generateCodec(TypeElement classElement) {
        currentClassElement = classElement;
        currentTypeSpec = TypeSpec.classBuilder(classElement.getSimpleName() + CODEC_SUFFIX);
        currentClassTypeName = TypeName.get(currentClassElement.asType());
        currentPackage = ((PackageElement)currentClassElement.getEnclosingElement()).getQualifiedName().toString();

        setClassHeader();
        addGetEncoderClassMethod();
        addEncodeMethod();
        addDecodeMethod();

        writeToFile();

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

        ParameterizedTypeName superType = ParameterizedTypeName.get(ClassName.get(Codec.class), currentClassTypeName);

        currentTypeSpec.addSuperinterface(superType);
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

    private void addEncodeMethod() {
        MethodSpec.Builder encodeMethod = MethodSpec.methodBuilder("encode")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(BsonWriter.class), "writer")
                .addParameter(currentClassTypeName, "value")
                .addParameter(TypeName.get(EncoderContext.class), "context");

        encodeMethod.addStatement("writer.writeStartDocument()");
        encodeFields(encodeMethod);
        encodeMethod.addStatement("writer.writeEndDocument()");
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
    }

    private void writeField(MethodSpec.Builder encodeMethod, VariableElement field, String getMethod) {
        try {
            PrimitiveType primitiveType = (PrimitiveType) field.asType();
            String writerMethod = getWriterMethodForType(primitiveType.toString());
            encodeMethod.addStatement("writer." + writerMethod + "("
                    + addModifiersForEncode(primitiveType.toString())
                    + ")", getMethod);
        } catch (ClassCastException | IllegalArgumentException e) {
            error(e.getMessage(), field);
        }
    }

    private String addModifiersForEncode(String type) {
        switch (type) {
            case "char":
                return "String.valueOf(value.$L)";
            default:
                return "value.$L";
        }
    }

    private String getWriterMethodForType(String type) throws IllegalArgumentException {
        switch (type) {
            case "byte":
            case "int":
            case "short":
                return "writeInt32";
            case "long":
                return "writeInt64";
            case "boolean":
                return "writeBoolean";
            case "char":
                return "writeString";
            case "double":
            case "float":
                return "writeDouble";
            default:
                throw new IllegalArgumentException("Unkown type: " + type);
        }
    }

    private Optional<String> methodThatReturnsValue(VariableElement field) {
        if (field.getModifiers().contains(Modifier.PUBLIC)) {
            return Optional.of(field.getSimpleName().toString());
        } else {
            StringBuilder transformedName = new StringBuilder(field.getSimpleName().toString());
            char firstChar = transformedName.charAt(0);
            firstChar = (char) (firstChar - 'a' + 'A');
            transformedName.setCharAt(0, firstChar);
            Element enclosingClass = field.getEnclosingElement();
            for (Element method : enclosingClass.getEnclosedElements()) {
                if (method.getKind().equals(ElementKind.METHOD)) {
                    String methodName = method.getSimpleName().toString();
                    if (methodName.equals("is" + transformedName) || methodName.equals("get" + transformedName))
                        return Optional.of(methodName + "()");
                }
            }
            warning("Couldn't find a way to get " + field.getSimpleName().toString() + "!\nThis can be easily fixed with a get method.", field);
            return Optional.empty();
        }
    }

    private void addDecodeMethod() {
        MethodSpec.Builder decodeMethod = MethodSpec.methodBuilder("decode")
                .returns(currentClassTypeName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(BsonReader.class), "reader")
                .addParameter(TypeName.get(DecoderContext.class), "context");

        TypeName builderType = ClassName.get(currentPackage, currentClassElement.getSimpleName() + BuilderGenerator.BUILDER_CLASS_SUFIX);
        decodeMethod
                .addStatement("$T builder = new $T()", builderType, builderType)
                .addStatement("reader.readStartDocument()")
        ;

        decodeFields(decodeMethod);

        decodeMethod
                .addStatement("reader.readEndDocument()")
                .addStatement("return builder.build()");

        currentTypeSpec.addMethod(decodeMethod.build());
    }

    private void decodeFields(MethodSpec.Builder decodeMethod) {
        decodeMethod
                .beginControlFlow("while (reader.readBsonType() != $T.END_OF_DOCUMENT)", ClassName.get(BsonType.class))
                .addStatement("String field = reader.readName()")
                .beginControlFlow("switch (field)");

        for (Element e : currentClassElement.getEnclosedElements()) {
            if ((e.getKind().isField())
                    && (e.getAnnotation(EgoIgnore.class)) == null) {
                VariableElement field = (VariableElement) e;
                decodeField(decodeMethod, field);
            }
        }

        decodeMethod
                .endControlFlow()
                .endControlFlow();
    }

    private void decodeField(MethodSpec.Builder decodeMethod, VariableElement field) {
        try {
            PrimitiveType primitiveType = (PrimitiveType) field.asType();
            String readerMethod = getReaderMethodForType(primitiveType.toString());
            decodeMethod
                    .addStatement("case $S:",field.getSimpleName())
                    .addStatement("builder.$L("
                            + addModifiersForDecode(primitiveType.toString(), readerMethod)
                            + ")", field.getSimpleName())
                    .addStatement("break");

        } catch (ClassCastException | IllegalArgumentException e) {
            error(e.getMessage(), field);
        }
        /*
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
    }

    private String getReaderMethodForType(String type) throws IllegalArgumentException {
        switch (type) {
            case "byte":
            case "int":
            case "short":
                return "readInt32";
            case "long":
                return "readInt64";
            case "boolean":
                return "readBoolean";
            case "char":
                return "readString";
            case "double":
            case "float":
                return "readDouble";
            default:
                throw new IllegalArgumentException("Unkown type: " + type);
        }
    }

    private String addModifiersForDecode(String type, String readerMethod) {
        switch (type) {
            case "char":
                return "reader." + readerMethod + "().charAt(0)";
            case "byte":
            case "float":
            case "short":
                return "(" + type + ") reader." + readerMethod + "()";
            default:
                return "reader." + readerMethod + "()";
        }
    }

    private void writeToFile() {
        JavaFile javaFile = JavaFile.builder(currentPackage + CODEC_PACKAGE_SUFFIX, currentTypeSpec.build()).build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
