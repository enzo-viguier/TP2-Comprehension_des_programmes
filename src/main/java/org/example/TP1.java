package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TP1 {
    public static void main(String[] args) {

        CodeAnalyzer analyzer = new CodeAnalyzer();

        int nbMethodeClasse = 2;

        try {
            analyzer.analyze("/home/e20200002449/IdeaProjects/TP2-Comprehension_des_programmes/src/main/java/" +
                    "");

            System.out.println("1. Nombre de classes : " + analyzer.getNumberOfClasses());
            System.out.println("2. Nombre de lignes de code : " + analyzer.getNumberOfLines());
            System.out.println("3. Nombre total de méthodes : " + analyzer.getNumberOfMethods());
            System.out.println("4. Nombre total de packages : " + analyzer.getNumberOfPackages());
            System.out.println("5. Nombre moyen de méthodes par classe : " + analyzer.getAverageMethodsPerClass());
            System.out.println("6. Nombre moyen de lignes de code par méthode : " + analyzer.getAverageLinesPerMethod());
            System.out.println("7. Nombre moyen d'attributs par classe : " + analyzer.getAverageFieldsPerClass());

            System.out.println("8. 10% des classes avec le plus grand nombre de méthodes : ");
            analyzer.getTop10PercentClassesByMethods().forEach(c -> System.out.println(c.getNameAsString()));

            System.out.println("9. 10% des classes avec le plus grand nombre d'attributs : ");
            analyzer.getTop10PercentClassesByFields().forEach(c -> System.out.println(c.getNameAsString()));

            System.out.println("10. Classes qui font partie des deux catégories : " + analyzer.getClassesInBothTop10Percent());

            System.out.println("11. Classes qui possèdent plus de " + nbMethodeClasse + " méthodes : ");
            analyzer.getClassesWithMoreThanXMethods(nbMethodeClasse).forEach(c -> System.out.println(c.getNameAsString()));


            System.out.println("12. Méthodes avec le plus grand nombre de lignes de code par classe : ");
            analyzer.classes.forEach(clazz -> {  // On parcourt toutes les classes analysées
                System.out.println("Classe : " + clazz.getNameAsString());

                // Récupérer les méthodes les plus longues pour cette classe
                List<MethodDeclaration> topMethods = analyzer.getTop10PercentMethodsByLines(clazz);

                if (topMethods.isEmpty()) {
                    System.out.println("  Aucune méthode.");
                } else {
                    topMethods.forEach(m -> {
                        int methodLines = m.getEnd().get().line - m.getBegin().get().line;
                        System.out.println("  Méthode : " + m.getNameAsString() + " (" + methodLines + " lignes)");
                    });
                }
            });

            System.out.println("13. Nombre maximal de paramètres par rapport à toutes les méthodes : " + analyzer.getMaxParametersPerMethod());

            CallGraphAnalyzer callGraphAnalyzer = new CallGraphAnalyzer();
            Map<String, List<String>> callGraph = callGraphAnalyzer.buildCallGraph(analyzer.classes);
            CallGraphAnalyzer.visualizeCallGraph(callGraph);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}