package com.github.albertosh.ego;

import com.github.albertosh.ego.generator.IEgoGenerator;
import com.github.albertosh.ego.generator.builder.BuilderGenerator;
import com.github.albertosh.ego.generator.codec.CodecGenerator;
import com.github.albertosh.ego.generator.create.CreateGenerator;
import com.github.albertosh.ego.generator.delete.DeleteGenerator;
import com.github.albertosh.ego.generator.filter.FilterFieldGenerator;
import com.github.albertosh.ego.generator.patch.PatchFieldGenerator;
import com.github.albertosh.ego.generator.patch.PatchGenerator;
import com.github.albertosh.ego.generator.read.ReadGenerator;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes({
        "com.github.albertosh.ego.Ego"
})
public class EgoCompiler extends AbstractProcessor {

    private final List<IEgoGenerator> generators = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Messager messager = processingEnv.getMessager();
        Filer filer = processingEnv.getFiler();
        CodecGenerator codecGenerator = new CodecGenerator(
                filer,
                messager,
                processingEnv.getTypeUtils());
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        BuilderGenerator builderGenerator = new BuilderGenerator(
                Names.instance(context),
                TreeMaker.instance(context),
                Trees.instance(processingEnv),
                messager,
                processingEnv.getTypeUtils(),
                filer
        );
        ReadGenerator readGenerator = new ReadGenerator(messager, filer);
        CreateGenerator createGenerator = new CreateGenerator(messager, filer);
        FilterFieldGenerator filterFieldGenerator = new FilterFieldGenerator(messager, filer);
        DeleteGenerator deleteGenerator = new DeleteGenerator(messager, filer);
        PatchFieldGenerator patchFieldGenerator = new PatchFieldGenerator(messager, filer);
        PatchGenerator patchGenerator = new PatchGenerator(messager, filer);

        generators.add(codecGenerator);
        generators.add(builderGenerator);
        generators.add(readGenerator);
        generators.add(createGenerator);
        generators.add(filterFieldGenerator);
        generators.add(deleteGenerator);
        generators.add(patchFieldGenerator);
        generators.add(patchGenerator);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(Ego.class)) {
            TypeElement classElement = (TypeElement) elem;
            generators.forEach(gen -> gen.generate(classElement));
        }

        return true;
    }

}
