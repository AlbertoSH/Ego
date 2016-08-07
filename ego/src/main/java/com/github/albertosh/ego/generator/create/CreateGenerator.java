package com.github.albertosh.ego.generator.create;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

public class CreateGenerator {

    public final static String CREATE_SUFFIX = "Create";

    private final CreateInterfaceGenerator createInterfaceGenerator;
    private final CreateImplementationGenerator createImplementationGenerator;

    public CreateGenerator(CreateInterfaceGenerator createInterfaceGenerator, CreateImplementationGenerator createImplementationGenerator) {
        this.createInterfaceGenerator = createInterfaceGenerator;
        this.createImplementationGenerator = createImplementationGenerator;
    }

    public CreateGenerator(Messager messager, Filer filer) {
        this.createInterfaceGenerator = new CreateInterfaceGenerator(messager, filer);
        this.createImplementationGenerator = new CreateImplementationGenerator(messager, filer);
    }

    public void generate(TypeElement classElement) {
        createInterfaceGenerator.generate(classElement);
        createImplementationGenerator.generate(classElement);
    }

}
