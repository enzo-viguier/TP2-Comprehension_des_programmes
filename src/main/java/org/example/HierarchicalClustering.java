package org.example;

import java.util.*;

public class HierarchicalClustering {
    private CouplingAnalyzer couplingAnalyzer;
    private List<Set<String>> clusters;

    public HierarchicalClustering(CouplingAnalyzer couplingAnalyzer, Set<String> classes) {
        this.couplingAnalyzer = couplingAnalyzer;
        this.clusters = new ArrayList<>();
        for (String className : classes) {
            Set<String> cluster = new HashSet<>();
            cluster.add(className);
            this.clusters.add(cluster);
        }
    }

    public List<Set<String>> performClustering() {
        while (clusters.size() > 1) {
            int bestI = -1;
            int bestJ = -1;
            double maxCoupling = -1;

            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double avgCoupling = calculateAverageCoupling(clusters.get(i), clusters.get(j));
                    if (avgCoupling > maxCoupling) {
                        maxCoupling = avgCoupling;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }

            if (bestI != -1 && bestJ != -1) {
                mergeClusters(bestI, bestJ);
            } else {
                break;
            }
        }

        return clusters;
    }

    private double calculateAverageCoupling(Set<String> cluster1, Set<String> cluster2) {
        double totalCoupling = 0;
        int count = 0;

        for (String class1 : cluster1) {
            for (String class2 : cluster2) {
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

    private void mergeClusters(int i, int j) {
        Set<String> mergedCluster = new HashSet<>(clusters.get(i));
        mergedCluster.addAll(clusters.get(j));
        clusters.remove(j);
        clusters.set(i, mergedCluster);
    }
}