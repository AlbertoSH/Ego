package com.github.albertosh.ego.generator.patch;

import com.github.albertosh.ego.generator.IEgoGenerator;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;

public class PatchGenerator implements IEgoGenerator {

    public final static String PATCH_SUFFIX = "Patch";

    private final PatchInterfaceGenerator patchInterfaceGenerator;
    private final PatchImplementationGenerator patchImplementationGenerator;

    public PatchGenerator(PatchInterfaceGenerator patchInterfaceGenerator, PatchImplementationGenerator patchImplementationGenerator) {
        this.patchInterfaceGenerator = patchInterfaceGenerator;
        this.patchImplementationGenerator = patchImplementationGenerator;
    }

    public PatchGenerator(Messager messager, Filer filer) {
        this.patchInterfaceGenerator = new PatchInterfaceGenerator(messager, filer);
        this.patchImplementationGenerator = new PatchImplementationGenerator(messager, filer);
    }

    @Override
    public void generate(TypeElement classElement) {
        patchInterfaceGenerator.generate(classElement);
        patchImplementationGenerator.generate(classElement);
    }

}
