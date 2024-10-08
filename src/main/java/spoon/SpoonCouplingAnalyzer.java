package spoon;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.code.CtInvocation;

import java.util.*;

public class SpoonCouplingAnalyzer {
    private Launcher launcher;
    private Map<String, Set<String>> methodCalls;
    private int totalRelations;

    public SpoonCouplingAnalyzer(String projectPath) {
        this.launcher = new Launcher();
        this.launcher.addInputResource(projectPath);
        this.launcher.buildModel();
        this.methodCalls = new HashMap<>();
        this.totalRelations = 0;
    }

    public void analyzeProject() {
        for (CtClass<?> ctClass : launcher.getModel().getElements(new TypeFilter<>(CtClass.class))) {
            String className = ctClass.getQualifiedName();
            for (CtMethod<?> method : ctClass.getMethods()) {
                String methodSignature = className + "." + method.getSignature();
                Set<String> calledMethods = new HashSet<>();

                for (CtInvocation<?> invocation : method.getElements(new TypeFilter<>(CtInvocation.class))) {
                    if (invocation.getExecutable().getDeclaringType() != null) {
                        String calledClass = invocation.getExecutable().getDeclaringType().getQualifiedName();
                        String calledMethod = calledClass + "." + invocation.getExecutable().getSignature();
                        calledMethods.add(calledMethod);
                    }
                }

                methodCalls.put(methodSignature, calledMethods);
                totalRelations += calledMethods.size();
            }
        }
    }

    public double calculateCoupling(String classA, String classB) {
        int relationsBetweenAB = 0;

        for (Map.Entry<String, Set<String>> entry : methodCalls.entrySet()) {
            String callerMethod = entry.getKey();
            Set<String> calledMethods = entry.getValue();

            if (callerMethod.startsWith(classA)) {
                for (String calledMethod : calledMethods) {
                    if (calledMethod.startsWith(classB)) {
                        relationsBetweenAB++;
                    }
                }
            } else if (callerMethod.startsWith(classB)) {
                for (String calledMethod : calledMethods) {
                    if (calledMethod.startsWith(classA)) {
                        relationsBetweenAB++;
                    }
                }
            }
        }

        return totalRelations > 0 ? (double) relationsBetweenAB / totalRelations : 0;
    }

}