package com.github.albertosh.ego;

import com.github.albertosh.ego.generator.builder.BuilderGenerator;
import com.github.albertosh.ego.generator.codec.CodecGenerator;
import com.github.albertosh.ego.generator.create.CreateGenerator;
import com.github.albertosh.ego.generator.filter.FilterFieldGenerator;
import com.github.albertosh.ego.generator.read.ReadGenerator;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

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

    private CodecGenerator codecGenerator;
    private BuilderGenerator builderGenerator;
    private ReadGenerator readGenerator;
    private CreateGenerator createGenerator;
    private FilterFieldGenerator filterFieldGenerator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Messager messager = processingEnv.getMessager();
        Filer filer = processingEnv.getFiler();
        codecGenerator = new CodecGenerator(
                filer,
                messager,
                processingEnv.getTypeUtils());
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        builderGenerator = new BuilderGenerator(
                Names.instance(context),
                TreeMaker.instance(context),
                Trees.instance(processingEnv),
                messager,
                processingEnv.getTypeUtils(),
                filer
        );
        readGenerator = new ReadGenerator(messager, filer);
        createGenerator = new CreateGenerator(messager, filer);
        filterFieldGenerator = new FilterFieldGenerator(messager, filer);

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(Ego.class)) {
            TypeElement classElement = (TypeElement) elem;
            codecGenerator.generate(classElement);
            builderGenerator.generate(classElement);
            readGenerator.generate(classElement);
            createGenerator.generate(classElement);
            filterFieldGenerator.generate(classElement);
        }

        return true;
    }

}
