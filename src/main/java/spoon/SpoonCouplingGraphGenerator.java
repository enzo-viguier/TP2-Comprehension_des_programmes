package spoon;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.code.CtInvocation;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class SpoonCouplingGraphGenerator {
    private Launcher launcher;
    private Map<String, Map<String, Integer>> couplingGraph;

    public SpoonCouplingGraphGenerator(String projectPath) {
        this.launcher = new Launcher();
        this.launcher.addInputResource(projectPath);
        this.launcher.buildModel();
        this.couplingGraph = new HashMap<>();
    }

    public void generateCouplingGraph() {
        for (CtClass<?> ctClass : launcher.getModel().getElements(new TypeFilter<>(CtClass.class))) {
            String className = ctClass.getQualifiedName();
            for (CtMethod<?> method : ctClass.getMethods()) {
                for (CtInvocation<?> invocation : method.getElements(new TypeFilter<>(CtInvocation.class))) {
                    if (invocation.getExecutable().getDeclaringType() != null) {
                        String calledClass = invocation.getExecutable().getDeclaringType().getQualifiedName();
                        if (!className.equals(calledClass)) {
                            couplingGraph.computeIfAbsent(className, k -> new HashMap<>())
                                    .merge(calledClass, 1, Integer::sum);
                        }
                    }
                }
            }
        }
    }

    public void visualizeCouplingGraph() {
        JFrame frame = new JFrame("Coupling Graph");
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Map<String, Integer>> entry : couplingGraph.entrySet()) {
            String className = entry.getKey();
            sb.append(className).append(" is coupled with:\n");
            for (Map.Entry<String, Integer> relation : entry.getValue().entrySet()) {
                String coupledClass = relation.getKey();
                int weight = relation.getValue();
                sb.append("  - ").append(coupledClass)
                        .append(" (coupling weight: ").append(weight).append(")\n");
            }
            sb.append("\n");
        }
        textArea.setText(sb.toString());

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}