package com.github.albertosh.ego.generator.delete;

import com.github.albertosh.ego.generator.IEgoGenerator;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

public class DeleteGenerator implements IEgoGenerator {

    public final static String DELETE_SUFFIX = "Delete";

    private final DeleteInterfaceGenerator deleteInterfaceGenerator;
    private final DeleteImplementationGenerator deleteImplementationGenerator;

    public DeleteGenerator(DeleteInterfaceGenerator deleteInterfaceGenerator, DeleteImplementationGenerator deleteImplementationGenerator) {
        this.deleteInterfaceGenerator = deleteInterfaceGenerator;
        this.deleteImplementationGenerator = deleteImplementationGenerator;
    }

    public DeleteGenerator(Messager messager, Filer filer) {
        this.deleteInterfaceGenerator = new DeleteInterfaceGenerator(messager, filer);
        this.deleteImplementationGenerator = new DeleteImplementationGenerator(messager, filer);
    }

    @Override
    public void generate(TypeElement classElement) {
        deleteInterfaceGenerator.generate(classElement);
        deleteImplementationGenerator.generate(classElement);
    }

}
