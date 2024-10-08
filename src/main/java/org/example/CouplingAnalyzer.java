package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class CouplingAnalyzer {

    private Map<String, Map<String, Integer>> classCouplings = new HashMap<>();
    private int totalRelations = 0;
    private Set<String> analyzedClasses = new HashSet<>();

    public void analyzeCoupling(List<CompilationUnit> compilationUnits) {
        for (CompilationUnit cu : compilationUnits) {
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(this::analyzeClass);
        }
    }

    private void analyzeClass(ClassOrInterfaceDeclaration clazz) {
        String className = clazz.getNameAsString();
        analyzedClasses.add(className);

        // Analyze fields
        clazz.getFields().forEach(field -> analyzeField(className, field));

        // Analyze methods
        clazz.getMethods().forEach(method -> analyzeMethod(className, method));
    }

    private void analyzeField(String className, FieldDeclaration field) {
        field.getVariables().forEach(var -> {
            String typeName = var.getType().asString();
            addRelation(className, typeName);
        });
    }

    private void analyzeMethod(String className, MethodDeclaration method) {
        // Analyze method parameters
        method.getParameters().forEach(param -> {
            String typeName = param.getType().asString();
            addRelation(className, typeName);
        });

        // Analyze method body
        method.findAll(MethodCallExpr.class).forEach(methodCall -> {
            methodCall.getScope().ifPresent(scope -> {
                String calledClass = scope.toString().split("\\.")[0];
                addRelation(className, calledClass);
            });
        });

        // Analyze local variable declarations
        method.findAll(ClassOrInterfaceType.class).forEach(type -> {
            addRelation(className, type.asString());
        });
    }

    private void addRelation(String fromClass, String toClass) {
        if (!fromClass.equals(toClass) && !toClass.startsWith("java.") && !toClass.equals("String")) {
            classCouplings.computeIfAbsent(fromClass, k -> new HashMap<>())
                    .merge(toClass, 1, Integer::sum);
            totalRelations++;
        }
    }

    public double calculateCoupling(String classA, String classB) throws ClassNotFoundException {
        if (!analyzedClasses.contains(classA)) {
            throw new ClassNotFoundException("Class " + classA + " not found in the analyzed classes.");
        }
        if (!analyzedClasses.contains(classB)) {
            throw new ClassNotFoundException("Class " + classB + " not found in the analyzed classes.");
        }

        int relationsBetweenAB = classCouplings.getOrDefault(classA, Collections.emptyMap())
                .getOrDefault(classB, 0) +
                classCouplings.getOrDefault(classB, Collections.emptyMap())
                        .getOrDefault(classA, 0);

        return totalRelations > 0 ? (double) relationsBetweenAB / totalRelations : 0;
    }

    public Map<String, Map<String, Integer>> getClassCouplings() {
        return classCouplings;
    }

    public int getTotalRelations() {
        return totalRelations;
    }

}