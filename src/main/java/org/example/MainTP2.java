package org.example;

import java.io.IOException;
import java.util.Map;

public class MainTP2 {

    public static void main(String[] args) throws IOException {
        // Analyser le code source avec CodeAnalyzer
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
        codeAnalyzer.analyze("src/main/java");

        // Analyser le couplage avec CouplingAnalyzer
        CouplingAnalyzer couplingAnalyzer = new CouplingAnalyzer();
        couplingAnalyzer.analyzeCoupling(codeAnalyzer.compilationUnits);

        // Obtenir et visualiser le graphe de couplage pondéré
        Map<String, Map<String, Integer>> couplingGraph = couplingAnalyzer.getClassCouplings();
        CallGraphAnalyzer.visualizeCouplingGraph(couplingGraph);
    }

}
