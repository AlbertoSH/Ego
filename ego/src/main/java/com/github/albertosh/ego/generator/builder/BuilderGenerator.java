package com.github.albertosh.ego.generator.builder;

import com.github.albertosh.ego.generator.EgoClassGenerator;
import com.github.albertosh.ego.generator.EgoGenerator;
import com.github.albertosh.ego.EgoObject;
import com.github.albertosh.ego.EgoObjectBuilder;
import com.github.albertosh.ego.persistence.builder.Builder;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Generated;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public class BuilderGenerator extends EgoClassGenerator {

    public final static String BUILDER_CLASS_SUFIX = "EgoBuilder";

    private final static String BUILDER_PACKAGE_SUFIX = "";
    private final Trees trees;
    private final TreeMaker make;
    private final Names names;
    private final Messager messager;
    private final Types types;
    private final Filer filer;
    private final List<TypeElement> pendingClasses;
    private final Map<TypeName, ClassName> alreadyGeneratedClasses2Builder;

    public BuilderGenerator(Names names, TreeMaker make, Trees trees, Messager messager, Types types, Filer filer) {
        super(messager, filer);
        this.names = names;
        this.make = make;
        this.trees = trees;
        this.messager = messager;
        this.types = types;
        this.filer = filer;
        this.pendingClasses = new ArrayList<TypeElement>();
        this.alreadyGeneratedClasses2Builder = new HashMap<TypeName, ClassName>();
    }

    @Override
    protected void doGenerate(TypeElement classElement) {
        pendingClasses.add(classElement);
        generateBuilders();
    }

    @Override
    protected String getPrefix() {
        return "";
    }

    @Override
    protected String getSuffix() {
        return BUILDER_CLASS_SUFIX;
    }

    private void generateBuilders() {
        int i = 0;
        while (!pendingClasses.isEmpty()) {
            TypeElement currentClass = pendingClasses.get(i);
            if (classExtendsEgoObject(currentClass) || superBuilderHasBeenAlreadyGenerated(currentClass)) {
                ClassName builderClass = generateBuilderForClass(currentClass);
                injectConstructor(currentClass, builderClass);
                pendingClasses.remove(i);
                alreadyGeneratedClasses2Builder.put(TypeName.get(currentClass.asType()), builderClass);
                int divider = pendingClasses.size();
                if (divider == 0)
                    divider = 1;
                i = i % divider;
            } else {
                // Skip this class. We'll try it again later
                int divider = pendingClasses.size();
                if (divider == 0)
                    divider = 1;
                i = (i + 1) % divider;
            }
        }
    }

    private boolean classDoesNotExtendEgoObject(TypeElement currentClass) {
        return !classExtendsEgoObject(currentClass);
    }

    private boolean classExtendsEgoObject(TypeElement currentClass) {
        TypeMirror superClass = currentClass.getSuperclass();
        return superClass.toString().equals(EgoObject.class.toString().replaceAll("class ", ""));
    }

    private boolean superBuilderHasBeenAlreadyGenerated(TypeElement currentClass) {
        TypeName superType = getSuperType(currentClass);
        return alreadyGeneratedClasses2Builder.keySet().contains(superType);
    }

    private TypeName getSuperType(Element classElement) {
        List<? extends TypeMirror> superClasses = types.directSupertypes(classElement.asType());
        TypeMirror superClass = superClasses.get(0);
        TypeName superType = ClassName.get(superClass);
        return superType;
    }

    private ClassName generateBuilderForClass(TypeElement currentClass) {
        String builderName = currentClass.getSimpleName() + BUILDER_CLASS_SUFIX;
        String packageName = currentClass.getEnclosingElement().toString() + BUILDER_PACKAGE_SUFIX;
        ClassName builderClass = ClassName.bestGuess(packageName + "." + builderName);

        TypeName currentType = TypeName.get(currentClass.asType());

        TypeSpec.Builder builder = TypeSpec.classBuilder(builderName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                        AnnotationSpec.builder(Generated.class)
                                .addMember("value", "\"Ego\"")
                                .build())
                .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build());

        if (classIsAbstract(currentClass))
            builder.addModifiers(Modifier.ABSTRACT);

        TypeVariableName typeVariableName = TypeVariableName.get("T", TypeName.get(currentClass.asType()));
        builder.addTypeVariable(typeVariableName);
        TypeName currentBuilderType = ParameterizedTypeName.get(builderClass, typeVariableName);

        addSuperClass(builder, currentClass, typeVariableName);
        //addSuperClass(builder, currentClass, TypeName.get(currentClass.asType()));

        MethodSpec.Builder fromPrototypeBuilder = MethodSpec.methodBuilder("fromPrototype")
//                .addAnnotation(OverridingMethodsMustInvokeSuper.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(currentBuilderType);


        fromPrototypeBuilder.addParameter(currentType, "prototype");


        if (classDoesNotExtendEgoObject(currentClass)) {
            fromPrototypeBuilder
                    .addStatement("super.fromPrototype(prototype)");
        }

        addFields(currentClass, builder, fromPrototypeBuilder, currentBuilderType);

        fromPrototypeBuilder.addStatement("return this");
        builder.addMethod(fromPrototypeBuilder.build());

        if (classIsNotAbstract(currentClass))
            addBuildMethod(currentClass, builder, typeVariableName);

        TypeSpec builded = builder.build();
        JavaFile javaFile = JavaFile.builder(packageName, builded)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builderClass;
    }

    private void addSuperClass(TypeSpec.Builder builder, TypeElement currentClass, TypeName typeVariableName) {
        if (classDoesNotExtendEgoObject(currentClass)) {
            TypeName superClassType = getSuperType(currentClass);
            ClassName superBuilderClassName = alreadyGeneratedClasses2Builder.get(superClassType);

            TypeName superType = ParameterizedTypeName.get(superBuilderClassName, typeVariableName);
            builder.superclass(superType);
        } else {

            ClassName builderImplClassName = ClassName.get(EgoObjectBuilder.class);
            ParameterizedTypeName parameterizedBuilder = ParameterizedTypeName.get(builderImplClassName, typeVariableName);
            builder.superclass(parameterizedBuilder);

        }

        ClassName builderClassName = ClassName.get(Builder.class);
        ParameterizedTypeName parameterizedIBuilder = ParameterizedTypeName.get(builderClassName, typeVariableName);
        builder.addSuperinterface(parameterizedIBuilder);

    }


    private boolean classIsAbstract(TypeElement currentClass) {
        return currentClass.getModifiers().contains(Modifier.ABSTRACT);
    }

    private boolean classIsNotAbstract(TypeElement currentClass) {
        return !classIsAbstract(currentClass);
    }


    private void addFields(Element currentClass, TypeSpec.Builder builder, MethodSpec.Builder fromPrototypeBuilder, TypeName currentBuilderType) {
        List<? extends Element> enclosed = currentClass.getEnclosedElements();
        for (Element field : enclosed) {
            if (field.getKind().isField()) {
                writeSingleAttributeSet((VariableElement) field, builder, fromPrototypeBuilder, currentBuilderType);
            }
        }
    }

    private void writeSingleAttributeSet(VariableElement field, TypeSpec.Builder builder, MethodSpec.Builder fromPrototypeBuilder, TypeName currentBuilderType) {
        String name = field.getSimpleName().toString();
        TypeName type = TypeName.get(field.asType());
        builder.addField(FieldSpec.builder(
                type, name, Modifier.PRIVATE
        ).build());


        builder.addMethod(MethodSpec
                .methodBuilder("get" + name.substring(0, 1).toUpperCase() + name.substring(1))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addStatement("return $L", name)
                .build());

        builder.addMethod(MethodSpec
                .methodBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .returns(currentBuilderType)
                .addParameter(type, name)
                .addStatement("this.$L = $L", name, name)
                .addStatement("return this")
                .build());

        Optional<String> method = methodThatReturnsValue(field);
        if (method.isPresent())
            fromPrototypeBuilder.addStatement("this.$L = prototype.$L", name, method.get());
    }

    private void addBuildMethod(Element currentClass, TypeSpec.Builder builder, TypeVariableName returningType) {
        TypeName elemType = TypeName.get(currentClass.asType());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("build")
                .addAnnotation(Override.class)
                .addAnnotation(
                        AnnotationSpec.builder(SuppressWarnings.class)
                                .addMember("value", "\"unchecked\"")
                                .build())
                .returns(returningType)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return (T) new $T(this)", elemType);
        builder.addMethod(methodBuilder.build());
    }


    private void injectConstructor(TypeElement currentClass, ClassName builderClass) {
        JCTree tree = (JCTree) trees.getTree(currentClass);
        TreeTranslator visitor = new ConstructorVisitor(currentClass, builderClass);
        tree.accept(visitor);
    }

    private class ConstructorVisitor extends TreeTranslator {

        private final TypeElement currentClass;
        private final ClassName builderClass;

        private ConstructorVisitor(TypeElement currentClass, ClassName builderClass) {
            this.currentClass = currentClass;
            this.builderClass = builderClass;
        }

        @Override
        public void visitClassDef(JCTree.JCClassDecl classNode) {
            super.visitClassDef(classNode);

            Iterator<JCTree> iterator = classNode.getMembers().iterator();
            List<JCTree> members = new ArrayList<>();
            iterator.forEachRemaining((member) -> {
                boolean addNode = true;
                if (member instanceof JCTree.JCMethodDecl) {
                    JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) member;
                    if (method.getName().equals(names.init)) {
                        if (method.getParameters().length() == 0) {
                            // Default constructor
                            // Inject stuff so it compiles but becomes useless at runtime
                            addNode = false;
                            members.add(defaultConstructorFixedNode());
                        }
                    }
                }
                if (addNode)
                    members.add(member);
            });
            members.add(getConstructorNode(classNode));
            JCTree[] asArray = members.toArray(new JCTree[members.size()]);
            classNode.defs = com.sun.tools.javac.util.List.from(asArray);
            result = classNode;
        }

        private JCTree defaultConstructorFixedNode() {
            JCTree.JCModifiers modifiers = make.Modifiers(2); // Private visibility

            ListBuffer<JCTree.JCStatement> nullChecks = new ListBuffer<JCTree.JCStatement>();
            ListBuffer<JCTree.JCStatement> assigns = new ListBuffer<JCTree.JCStatement>();
            ListBuffer<JCTree.JCVariableDecl> params = new ListBuffer<JCTree.JCVariableDecl>();

            addDefaultSuperInvocation(assigns);
            throwException(assigns);

            return make.MethodDef(modifiers, names.init,
                    null, com.sun.tools.javac.util.List.<JCTree.JCTypeParameter>nil(), params.toList(), com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(),
                    make.Block(0L, nullChecks.appendList(assigns).toList()), null);
        }

        private void addDefaultSuperInvocation(ListBuffer<JCTree.JCStatement> assigns) {
            JCTree.JCExpression builderParam = make.Literal("");
            JCTree.JCIdent sup = make.Ident(names._super);
            JCTree.JCMethodInvocation invocation = make.Apply(com.sun.tools.javac.util.List.nil(), sup, com.sun.tools.javac.util.List.of(builderParam));
            JCTree.JCExpressionStatement exec = make.Exec(invocation);
            assigns.append(exec);
        }

        private void throwException(ListBuffer<JCTree.JCStatement> assigns) {
            JCTree.JCExpression textArg = make.Literal("This constructor is just for the compiler not to complain.\n" +
                    "It\'s not intended to be used!");

            JCTree.JCExpression javaPackage = make.Ident(names.fromString("java"));
            JCTree.JCExpression javaLangPackage = make.Select(javaPackage, names.fromString("lang"));

            com.sun.tools.javac.util.List<JCTree.JCExpression> typeArgs =
                    com.sun.tools.javac.util.List
                            .of(make.Select(javaLangPackage, names.fromString("String")));

            JCTree.JCExpression clazz = make.Select(javaLangPackage, names.fromString("RuntimeException"));
            com.sun.tools.javac.util.List<JCTree.JCExpression> args =
                    com.sun.tools.javac.util.List.of(textArg);
            JCTree.JCNewClass newClass = make.NewClass(null, typeArgs, clazz, args, null);
            JCTree.JCThrow throwNode = make.Throw(newClass);
            assigns.add(throwNode);
        }


        private JCTree getConstructorNode(JCTree.JCClassDecl classNode) {
            JCTree.JCModifiers modifiers = make.Modifiers(0); // Default visibility

            ListBuffer<JCTree.JCStatement> nullChecks = new ListBuffer<JCTree.JCStatement>();
            ListBuffer<JCTree.JCStatement> assigns = new ListBuffer<JCTree.JCStatement>();
            ListBuffer<JCTree.JCVariableDecl> params = new ListBuffer<JCTree.JCVariableDecl>();

            fillParams(params);

            addSuperInvocation(assigns);

            fillAssigns(classNode, assigns);

            return make.MethodDef(modifiers, names.init,
                    null, com.sun.tools.javac.util.List.<JCTree.JCTypeParameter>nil(), params.toList(), com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(),
                    make.Block(0L, nullChecks.appendList(assigns).toList()), null);
        }

        private void addSuperInvocation(ListBuffer<JCTree.JCStatement> assigns) {
            JCTree.JCExpression builderParam = make.Ident(names.fromString("builder"));
            JCTree.JCIdent sup = make.Ident(names._super);
            JCTree.JCMethodInvocation invocation = make.Apply(com.sun.tools.javac.util.List.nil(), sup, com.sun.tools.javac.util.List.of(builderParam));
            JCTree.JCExpressionStatement exec = make.Exec(invocation);
            assigns.append(exec);
        }

        private void fillParams(ListBuffer<JCTree.JCVariableDecl> params) {
            JCTree.JCExpression paramClass = null;
            String[] split = builderClass.toString().split("\\.");
            for (String segment : split) {
                if (paramClass == null)
                    paramClass = make.Ident(names.fromString(segment));
                else
                    paramClass = make.Select(paramClass, names.fromString(segment));
            }

            long flags = Flags.PARAMETER;
            JCTree.JCModifiers modifiers = make.Modifiers(flags);
            Name name = names.fromString("builder");
            JCTree.JCVariableDecl param = make.VarDef(modifiers, name, paramClass, null);

            params.add(param);
        }

        private void fillAssigns(JCTree.JCClassDecl classNode, ListBuffer<JCTree.JCStatement> assigns) {
            Iterator<JCTree> iterator = classNode.getMembers().iterator();
            List<JCTree> members = new ArrayList<JCTree>();
            iterator.forEachRemaining(jcTree -> {
                members.add(jcTree);
                if (jcTree.getKind().equals(Tree.Kind.VARIABLE)) {
                    JCTree.JCVariableDecl var = (JCTree.JCVariableDecl) jcTree;
                    JCTree.JCFieldAccess thisX = make.Select(make.Ident(names._this), var.getName());

                    JCTree.JCExpression builder = make.Ident(names.fromString("builder"));
                    String nameAsString = var.getName().toString();
                    String methodName = "get" + nameAsString.substring(0, 1).toUpperCase() + nameAsString.substring(1);
                    JCTree.JCExpression method = make.Select(builder, names.fromString(methodName));
                    JCTree.JCMethodInvocation invocation = make.Apply(com.sun.tools.javac.util.List.nil(), method, com.sun.tools.javac.util.List.nil());
                    JCTree.JCExpressionStatement exec = make.Exec(invocation);
                    JCTree.JCExpression assign = make.Assign(thisX, exec.getExpression());
                    assigns.append(make.Exec(assign));
                }
            });
        }
    }


}
