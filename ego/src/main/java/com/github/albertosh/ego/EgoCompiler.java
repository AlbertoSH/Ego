package com.github.albertosh.ego;

import com.github.albertosh.ego.builder.BuilderGenerator;
import com.github.albertosh.ego.codec.CodecGenerator;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
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

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        codecGenerator = new CodecGenerator(
                processingEnv.getFiler(),
                processingEnv.getMessager(),
                processingEnv.getTypeUtils());
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        builderGenerator = new BuilderGenerator(
                Names.instance(context),
                TreeMaker.instance(context),
                Trees.instance(processingEnv),
                processingEnv.getMessager(),
                processingEnv.getTypeUtils(),
                processingEnv.getFiler()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element elem : roundEnv.getElementsAnnotatedWith(Ego.class)) {
            TypeElement classElement = (TypeElement) elem;
            codecGenerator.generateCodec(classElement);
            builderGenerator.generateBuilder(classElement);
        }

        return true;
    }

}
