package org.example;

import java.util.*;

public class HierarchicalClustering {
    private Map<String, Map<String, Double>> couplingMatrix;
    private List<Cluster> clusters;

    public HierarchicalClustering(CouplingAnalyzer couplingAnalyzer, Set<String> classNames) {
        this.couplingMatrix = new HashMap<>();
        this.clusters = new ArrayList<>();

        // Initialize coupling matrix
        for (String class1 : classNames) {
            for (String class2 : classNames) {
                if (!class1.equals(class2)) {
                    try {
                        double coupling = couplingAnalyzer.calculateCoupling(class1, class2);
                        couplingMatrix.computeIfAbsent(class1, k -> new HashMap<>()).put(class2, coupling);
                    } catch (ClassNotFoundException e) {
                        // Handle the exception (e.g., log it or ignore)
                    }
                }
            }
        }

        // Initialize clusters
        for (String className : classNames) {
            clusters.add(new Cluster(className));
        }
    }

    public List<Set<String>> performClustering() {
        while (clusters.size() > 1) {
            double maxCoupling = -1;
            Cluster cluster1 = null;
            Cluster cluster2 = null;

            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double coupling = calculateClusterCoupling(clusters.get(i), clusters.get(j));
                    if (coupling > maxCoupling) {
                        maxCoupling = coupling;
                        cluster1 = clusters.get(i);
                        cluster2 = clusters.get(j);
                    }
                }
            }

            if (cluster1 != null && cluster2 != null) {
                Cluster newCluster = new Cluster(cluster1, cluster2);
                clusters.remove(cluster1);
                clusters.remove(cluster2);
                clusters.add(newCluster);
            } else {
                break;
            }
        }

        // Convert clusters to List<Set<String>>
        List<Set<String>> result = new ArrayList<>();
        for (Cluster cluster : clusters) {
            result.add(cluster.getClasses());
        }
        return result;
    }

    private double calculateClusterCoupling(Cluster c1, Cluster c2) {
        double totalCoupling = 0;
        int count = 0;

        for (String class1 : c1.getClasses()) {
            for (String class2 : c2.getClasses()) {
                if (couplingMatrix.containsKey(class1) && couplingMatrix.get(class1).containsKey(class2)) {
                    totalCoupling += couplingMatrix.get(class1).get(class2);
                    count++;
                }
            }
        }

        return count > 0 ? totalCoupling / count : 0;
    }

    static class Cluster {
        private Set<String> classes;
        Cluster left;
        Cluster right;

        public Cluster(String className) {
            this.classes = new HashSet<>(Collections.singletonList(className));
        }

        public Cluster(Cluster c1, Cluster c2) {
            this.classes = new HashSet<>(c1.classes);
            this.classes.addAll(c2.classes);
            this.left = c1;
            this.right = c2;
        }

        public Set<String> getClasses() {
            return classes;
        }
    }

    public void printDendrogram() {
        if (!clusters.isEmpty()) {
            printDendrogramRecursive(clusters.get(0), "", true);
        }
    }

    private void printDendrogramRecursive(Cluster cluster, String indent, boolean last) {
        System.out.print(indent);
        if (last) {
            System.out.print("└─");
            indent += "  ";
        } else {
            System.out.print("├─");
            indent += "│ ";
        }

        if (cluster.left == null && cluster.right == null) {
            System.out.println(cluster.classes.iterator().next());
        } else {
            System.out.println("Cluster");
            printDendrogramRecursive(cluster.left, indent, false);
            printDendrogramRecursive(cluster.right, indent, true);
        }
    }
}