package org.example;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;



import javax.swing.JFrame;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CallGraphAnalyzer {

    private Map<String, List<String>> callGraph = new HashMap<>();

    public Map<String, List<String>> buildCallGraph(List<ClassOrInterfaceDeclaration> classes) {
        for (ClassOrInterfaceDeclaration clazz : classes) {
            for (MethodDeclaration method : clazz.getMethods()) {
                String methodName = clazz.getNameAsString() + "." + method.getNameAsString();

                MethodCallCollector collector = new MethodCallCollector();
                collector.visit(method, null);

                List<String> calledMethods = collector.getCalledMethods();
                callGraph.put(methodName, calledMethods);
            }
        }
        return callGraph;
    }

    private static class MethodCallCollector extends VoidVisitorAdapter<Void> {
        private List<String> calledMethods = new ArrayList<>();

        @Override
        public void visit(MethodCallExpr methodCall, Void arg) {
            super.visit(methodCall, arg);
            calledMethods.add(methodCall.getNameAsString()); // Nom de la méthode appelée
        }

        public List<String> getCalledMethods() {
            return calledMethods;
        }
    }

    public static void visualizeCallGraph(Map<String, List<String>> callGraph) {
        JFrame frame = new JFrame("Call Graph");
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : callGraph.entrySet()) {
            sb.append(entry.getKey()).append(" calls:\n");
            for (String callee : entry.getValue()) {
                sb.append("  - ").append(callee).append("\n");
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
