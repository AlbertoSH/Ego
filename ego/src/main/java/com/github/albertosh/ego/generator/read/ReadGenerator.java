package com.github.albertosh.ego.generator.read;

import com.github.albertosh.ego.generator.IEgoGenerator;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

public class ReadGenerator implements IEgoGenerator {

    public final static String READ_SUFFIX = "Read";

    private final ReadInterfaceGenerator readInterfaceGenerator;
    private final ReadImplementationGenerator readImplementationGenerator;

    public ReadGenerator(ReadInterfaceGenerator readInterfaceGenerator, ReadImplementationGenerator readImplementationGenerator) {
        this.readInterfaceGenerator = readInterfaceGenerator;
        this.readImplementationGenerator = readImplementationGenerator;
    }

    public ReadGenerator(Messager messager, Filer filer) {
        this.readInterfaceGenerator = new ReadInterfaceGenerator(messager, filer);
        this.readImplementationGenerator = new ReadImplementationGenerator(messager, filer);
    }

    @Override
    public void generate(TypeElement classElement) {
        readInterfaceGenerator.generate(classElement);
        readImplementationGenerator.generate(classElement);
    }

}
