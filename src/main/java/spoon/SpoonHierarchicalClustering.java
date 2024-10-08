package spoon;

import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.code.CtInvocation;

import java.util.*;

public class SpoonHierarchicalClustering {
    private Launcher launcher;
    private Map<String, Map<String, Double>> couplingMatrix;
    private List<Cluster> clusters;

    public SpoonHierarchicalClustering(String projectPath) {
        this.launcher = new Launcher();
        this.launcher.addInputResource(projectPath);
        this.launcher.buildModel();
        this.couplingMatrix = new HashMap<>();
        this.clusters = new ArrayList<>();
    }

    public void generateCouplingMatrix() {
        Map<String, Map<String, Integer>> callCount = new HashMap<>();
        int totalCalls = 0;

        for (CtClass<?> ctClass : launcher.getModel().getElements(new TypeFilter<>(CtClass.class))) {
            String className = ctClass.getQualifiedName();
            for (CtMethod<?> method : ctClass.getMethods()) {
                for (CtInvocation<?> invocation : method.getElements(new TypeFilter<>(CtInvocation.class))) {
                    if (invocation.getExecutable().getDeclaringType() != null) {
                        String calledClass = invocation.getExecutable().getDeclaringType().getQualifiedName();
                        if (!className.equals(calledClass)) {
                            callCount.computeIfAbsent(className, k -> new HashMap<>())
                                    .merge(calledClass, 1, Integer::sum);
                            totalCalls++;
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, Map<String, Integer>> entry : callCount.entrySet()) {
            String className = entry.getKey();
            for (Map.Entry<String, Integer> calls : entry.getValue().entrySet()) {
                String calledClass = calls.getKey();
                double coupling = (double) calls.getValue() / totalCalls;
                couplingMatrix.computeIfAbsent(className, k -> new HashMap<>()).put(calledClass, coupling);
                couplingMatrix.computeIfAbsent(calledClass, k -> new HashMap<>()).put(className, coupling);
            }
        }

        for (String className : couplingMatrix.keySet()) {
            clusters.add(new Cluster(className));
        }
    }

    public List<Cluster> performClustering() {
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

        return clusters;
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

    public void printDendrogram(Cluster cluster, String indent, boolean last) {
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
            printDendrogram(cluster.left, indent, false);
            printDendrogram(cluster.right, indent, true);
        }
    }

    public double getCouplingBetweenClasses(String class1, String class2) {
        if (couplingMatrix.containsKey(class1) && couplingMatrix.get(class1).containsKey(class2)) {
            return couplingMatrix.get(class1).get(class2);
        }
        return 0.0;
    }

    // Rendons la couplingMatrix accessible
    public Map<String, Map<String, Double>> getCouplingMatrix() {
        return couplingMatrix;
    }


}