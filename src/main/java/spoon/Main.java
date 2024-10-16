package spoon;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar StatsAppOO.jar <projectPath> [CP]");
            System.out.println("  projectPath: chemin vers le répertoire du projet à analyser");
            System.out.println("  CP: (optionnel) valeur du seuil de couplage (par défaut: 0.1)");
            return;
        }

        String projectPath = args[0];
        double CP = 0.1; // Valeur par défaut

        if (args.length > 1) {
            try {
                CP = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Erreur : Le CP doit être un nombre décimal valide. Utilisation de la valeur par défaut (0.1).");
            }
        }

        System.out.println("Analyse du projet : " + projectPath);
        System.out.println("Valeur de CP : " + CP);

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

        SpoonModuleIdentifier moduleIdentifier = new SpoonModuleIdentifier(projectPath, CP);
        moduleIdentifier.identifyModules();
        moduleIdentifier.printModules();
    }
}