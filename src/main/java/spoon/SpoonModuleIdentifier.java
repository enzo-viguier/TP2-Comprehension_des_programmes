package spoon;

import java.util.*;

public class SpoonModuleIdentifier {
    private SpoonHierarchicalClustering clustering;
    private double CP;
    private int maxModules;
    private List<Set<String>> modules;

    public SpoonModuleIdentifier(String projectPath, double CP) {
        this.clustering = new SpoonHierarchicalClustering(projectPath);
        this.CP = CP;
        this.modules = new ArrayList<>();
    }

    public List<Set<String>> identifyModules() {
        clustering.generateCouplingMatrix();
        List<SpoonHierarchicalClustering.Cluster> clusters = clustering.performClustering();

        int totalClasses = countTotalClasses(clusters.get(0));
        this.maxModules = totalClasses / 2;

        identifyModulesRecursive(clusters.get(0));

        // Si nous avons plus de modules que maxModules, fusionnons les plus petits
        while (modules.size() > maxModules) {
            mergeTwoSmallestModules();
        }

        return modules;
    }

    private void identifyModulesRecursive(SpoonHierarchicalClustering.Cluster cluster) {
        if (isValidModule(cluster)) {
            modules.add(cluster.getClasses());
        } else if (cluster.left != null && cluster.right != null) {
            identifyModulesRecursive(cluster.left);
            identifyModulesRecursive(cluster.right);
        }
    }

    private boolean isValidModule(SpoonHierarchicalClustering.Cluster cluster) {
        if (cluster.getClasses().size() == 1) {
            return true; // Une seule classe est toujours un module valide
        }

        double avgCoupling = calculateAverageCoupling(cluster.getClasses());
        return avgCoupling > CP;
    }

    private double calculateAverageCoupling(Set<String> classes) {
        double totalCoupling = 0;
        int count = 0;

        for (String class1 : classes) {
            for (String class2 : classes) {
                if (!class1.equals(class2)) {
                    totalCoupling += clustering.getCouplingBetweenClasses(class1, class2);
                    count++;
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

    private int countTotalClasses(SpoonHierarchicalClustering.Cluster cluster) {
        return cluster.getClasses().size();
    }

    public void printModules() {
        for (int i = 0; i < modules.size(); i++) {
            System.out.println("Module " + (i + 1) + ":");
            for (String className : modules.get(i)) {
                System.out.println("  - " + className);
            }
            System.out.println();
        }
    }


}