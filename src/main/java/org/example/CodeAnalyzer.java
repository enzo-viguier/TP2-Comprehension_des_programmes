package org.example;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CodeAnalyzer {

    List<CompilationUnit> compilationUnits = new ArrayList<>();
    List<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
    private int totalLines = 0;
    private int totalMethods = 0;
    private int totalFields = 0;
    private Set<String> packages = new HashSet<>();

    public void analyze(String directoryPath) throws IOException {
        compilationUnits.clear();
        classes.clear();
        totalLines = 0;
        totalMethods = 0;
        totalFields = 0;
        packages.clear();

        Files.walk(Paths.get(directoryPath))
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(path.toFile());
                        compilationUnits.add(cu);
                        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classes::add);
                        cu.findAll(MethodDeclaration.class).forEach(md -> totalMethods++);
                        cu.findAll(FieldDeclaration.class).forEach(fd -> totalFields += fd.getVariables().size());
                        cu.getPackageDeclaration().ifPresent(pd -> packages.add(pd.getNameAsString()));

                        totalLines += Files.readAllLines(path).size();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public int getNumberOfClasses() {
        return classes.size();
    }

    public int getNumberOfLines() {
        return totalLines;
    }

    public int getNumberOfMethods() {
        return totalMethods;
    }

    public int getNumberOfPackages() {
        return packages.size();
    }

    public double getAverageMethodsPerClass() {
        return (double) totalMethods / getNumberOfClasses();
    }

    public double getAverageLinesPerMethod() {
        return (double) totalLines / totalMethods;
    }

    public double getAverageFieldsPerClass() {
        return (double) totalFields / getNumberOfClasses();
    }

    public List<ClassOrInterfaceDeclaration> getTop10PercentClassesByMethods() {
        int limit = Math.max(1, (int) Math.ceil(0.1 * classes.size()));

        return classes.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getMethods().size(), c1.getMethods().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<ClassOrInterfaceDeclaration> getTop10PercentClassesByFields() {
        int limit = Math.max(1, (int) Math.ceil(0.1 * classes.size()));

        return classes.stream()
                .sorted((c1, c2) -> Integer.compare(c2.getFields().size(), c1.getFields().size()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public String getClassesInBothTop10Percent() {
        List<ClassOrInterfaceDeclaration> topByMethods = getTop10PercentClassesByMethods();
        List<ClassOrInterfaceDeclaration> topByFields = getTop10PercentClassesByFields();

        List<ClassOrInterfaceDeclaration> classesInBoth = topByMethods.stream()
                .filter(topByFields::contains)
                .collect(Collectors.toList());
        if (classesInBoth.isEmpty()) {
            return "Aucune classe";
        }
        return classesInBoth.stream()
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .collect(Collectors.joining(", "));
    }


    public List<ClassOrInterfaceDeclaration> getClassesWithMoreThanXMethods(int X) {
        return classes.stream()
                .filter(c -> c.getMethods().size() > X)
                .collect(Collectors.toList());
    }

    public List<MethodDeclaration> getTop10PercentMethodsByLines(ClassOrInterfaceDeclaration clazz) {
        int limit = Math.max(1, (int) Math.ceil(0.1 * clazz.getMethods().size()));

        return clazz.getMethods().stream()
                .sorted(Comparator.comparingInt(md -> md.getEnd().get().line - md.getBegin().get().line))
                .limit(limit)
                .collect(Collectors.toList());
    }



    public int getMaxParametersPerMethod() {
        return classes.stream()
                .flatMap(c -> c.getMethods().stream())
                .mapToInt(md -> md.getParameters().size())
                .max()
                .orElse(0);
    }

}
