package org.example;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        CodeAnalyzer codeAnalyzer = new CodeAnalyzer();
        codeAnalyzer.analyze("src/main/java");




        CouplingAnalyzer couplingAnalyzer = new CouplingAnalyzer();
        couplingAnalyzer.analyzeCoupling(codeAnalyzer.compilationUnits);
        try {
            String classA = "Book";
            String classB = "Library";
            double classCoupling = couplingAnalyzer.calculateCoupling(classA, classB);
            System.out.println("1. Couplage entre " + classA + " et " + classB + " : " + classCoupling*100 + "%");
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur lors du calcul du couplage : " + e.getMessage());
        }

        // Obtenir et visualiser le graphe de couplage pondéré
        Map<String, Map<String, Integer>> couplingGraph = couplingAnalyzer.getClassCouplings();
        CallGraphAnalyzer.visualizeCouplingGraph(couplingGraph);


        Set<String> allClasses = new HashSet<>(codeAnalyzer.classes.stream()
                .map(c -> c.getNameAsString())
                .collect(Collectors.toSet()));

        HierarchicalClustering clustering = new HierarchicalClustering(couplingAnalyzer, allClasses);
        List<Set<String>> finalClusters = clustering.performClustering();

        System.out.println("2. Résultat du clustering hiérarchique :");
        for (int i = 0; i < finalClusters.size(); i++) {
            System.out.println("Cluster " + (i + 1) + ": " + String.join(", ", finalClusters.get(i)));
        }


        List<String> classNames = codeAnalyzer.classes.stream()
                .map(clazz -> clazz.getNameAsString())
                .collect(Collectors.toList());

        ModuleIdentifier moduleIdentifier = new ModuleIdentifier(
                couplingAnalyzer,
                classNames,
                0.001 // CP = 0.1, ajustez selon vos besoins
        );

        List<Set<String>> modules = moduleIdentifier.identifyModules();

        System.out.println("3. Modules identifiés :");
        for (int i = 0; i < modules.size(); i++) {
            System.out.println("Module " + (i + 1) + ": " + String.join(", ", modules.get(i)));
        }
    }

}
