package org.example;

import java.util.*;

public class ModuleIdentifier {
    private CouplingAnalyzer couplingAnalyzer;
    private List<String> classNames;
    private double CP;
    private int maxModules;
    private List<Set<String>> modules;

    public ModuleIdentifier(CouplingAnalyzer couplingAnalyzer, List<String> classNames, double CP) {
        this.couplingAnalyzer = couplingAnalyzer;
        this.classNames = classNames;
        this.CP = CP;
        this.modules = new ArrayList<>();
        this.maxModules = classNames.size() / 2;
    }

    public List<Set<String>> identifyModules() {
        List<Cluster> clusters = performClustering();
        identifyModulesRecursive(clusters.get(0));

        while (modules.size() > maxModules) {
            mergeTwoSmallestModules();
        }

        return modules;
    }

    private List<Cluster> performClustering() {
        List<Cluster> clusters = new ArrayList<>();
        for (String className : classNames) {
            clusters.add(new Cluster(new HashSet<>(Collections.singletonList(className))));
        }

        while (clusters.size() > 1) {
            double maxCoupling = -1;
            int maxI = -1, maxJ = -1;

            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double coupling = calculateAverageCoupling(clusters.get(i).classes, clusters.get(j).classes);
                    if (coupling > maxCoupling) {
                        maxCoupling = coupling;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }

            Cluster newCluster = new Cluster(clusters.get(maxI), clusters.get(maxJ));
            clusters.remove(maxJ);
            clusters.remove(maxI);
            clusters.add(newCluster);
        }

        return clusters;
    }

    private void identifyModulesRecursive(Cluster cluster) {
        if (isValidModule(cluster)) {
            modules.add(cluster.classes);
        } else if (cluster.left != null && cluster.right != null) {
            identifyModulesRecursive(cluster.left);
            identifyModulesRecursive(cluster.right);
        }
    }

    private boolean isValidModule(Cluster cluster) {
        if (cluster.classes.size() == 1) {
            return true;
        }

        double avgCoupling = calculateAverageCoupling(cluster.classes);
        return avgCoupling > CP;
    }

    private double calculateAverageCoupling(Set<String> classes) {
        return calculateAverageCoupling(classes, classes);
    }

    private double calculateAverageCoupling(Set<String> classes1, Set<String> classes2) {
        double totalCoupling = 0;
        int count = 0;

        for (String class1 : classes1) {
            for (String class2 : classes2) {
                if (!class1.equals(class2)) {
                    try {
                        totalCoupling += couplingAnalyzer.calculateCoupling(class1, class2);
                        count++;
                    } catch (ClassNotFoundException e) {
                        // Ignore classes not found
                    }
                }
            }
        }

        return count > 0 ? totalCoupling / count : 0;
    }

    private void mergeTwoSmallestModules() {
        if (modules.size() < 2) return;

        int smallestIndex1 = 0;
        int smallestIndex2 = 1;
        int smallestSize1 = modules.get(0).size();
        int smallestSize2 = modules.get(1).size();

        for (int i = 1; i < modules.size(); i++) {
            int size = modules.get(i).size();
            if (size < smallestSize1) {
                smallestSize2 = smallestSize1;
                smallestIndex2 = smallestIndex1;
                smallestSize1 = size;
                smallestIndex1 = i;
            } else if (size < smallestSize2) {
                smallestSize2 = size;
                smallestIndex2 = i;
            }
        }

        Set<String> mergedModule = new HashSet<>(modules.get(smallestIndex1));
        mergedModule.addAll(modules.get(smallestIndex2));
        modules.remove(Math.max(smallestIndex1, smallestIndex2));
        modules.remove(Math.min(smallestIndex1, smallestIndex2));
        modules.add(mergedModule);
    }

    private static class Cluster {
        Set<String> classes;
        Cluster left;
        Cluster right;

        Cluster(Set<String> classes) {
            this.classes = classes;
        }

        Cluster(Cluster left, Cluster right) {
            this.left = left;
            this.right = right;
            this.classes = new HashSet<>(left.classes);
            this.classes.addAll(right.classes);
        }
    }
}