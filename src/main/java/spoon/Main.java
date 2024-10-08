package spoon;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        String projectPath = "src/main/java";
        SpoonCouplingAnalyzer analyzer = new SpoonCouplingAnalyzer(projectPath);
        analyzer.analyzeProject();

        String classA = "library.Book";
        String classB = "library.Library";
        double coupling = analyzer.calculateCoupling(classA, classB);

        System.out.printf("Le couplage entre %s et %s est : %.2f%%\n", classA, classB, coupling * 100);

        SpoonCouplingGraphGenerator generator = new SpoonCouplingGraphGenerator(projectPath);
        generator.generateCouplingGraph();
        generator.visualizeCouplingGraph();

        SpoonHierarchicalClustering clustering = new SpoonHierarchicalClustering(projectPath);
        clustering.generateCouplingMatrix();
        List<SpoonHierarchicalClustering.Cluster> result = clustering.performClustering();
        clustering.printDendrogram(result.get(0), "", true);


        double CP = 0.1; // Ajustez ce paramètre selon vos besoins
        SpoonModuleIdentifier moduleIdentifier = new SpoonModuleIdentifier(projectPath, CP);
        moduleIdentifier.identifyModules();
        moduleIdentifier.printModules();


    }
}