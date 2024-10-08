package org.example;

import java.util.*;

public class ModuleIdentifier {
    private final CouplingAnalyzer couplingAnalyzer;
    private final List<String> classes;
    private final double CP;
    private final int maxModules;

    public ModuleIdentifier(CouplingAnalyzer couplingAnalyzer, List<String> classes, double CP) {
        this.couplingAnalyzer = couplingAnalyzer;
        this.classes = new ArrayList<>(classes);
        this.CP = CP;
        this.maxModules = classes.size() / 2;
    }

    public List<Set<String>> identifyModules() {
        List<Set<String>> modules = new ArrayList<>();
        for (String className : classes) {
            modules.add(new HashSet<>(Collections.singletonList(className)));
        }

        while (modules.size() > 1 && modules.size() > maxModules) {
            int bestI = -1;
            int bestJ = -1;
            double bestCoupling = -1;

            for (int i = 0; i < modules.size(); i++) {
                for (int j = i + 1; j < modules.size(); j++) {
                    double avgCoupling = calculateAverageCoupling(modules.get(i), modules.get(j));
                    if (avgCoupling > bestCoupling) {
                        bestCoupling = avgCoupling;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            if (bestCoupling > CP) {
                Set<String> mergedModule = new HashSet<>(modules.get(bestI));
                mergedModule.addAll(modules.get(bestJ));
                modules.remove(bestJ);
                modules.set(bestI, mergedModule);
            } else {
                break;
            }
        }

        return modules;
    }

    private double calculateAverageCoupling(Set<String> module1, Set<String> module2) {
        double totalCoupling = 0;
        int count = 0;

        for (String class1 : module1) {
            for (String class2 : module2) {
                try {
                    totalCoupling += couplingAnalyzer.calculateCoupling(class1, class2);
                    count++;
                } catch (ClassNotFoundException e) {
                    // Ignore classes not found
                }
            }
        }

        return count > 0 ? totalCoupling / count : 0;
    }
}