package be.wacken.planner.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.base.DescribedPredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.ToolProvider;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class HexagonalArchitectureTest {
    private static final String ROOT = "be.wacken.planner";

    @Test
    void allJvmMainClassesRespectHexagonalBoundaries() throws Exception {
        List<Path> directories = new ArrayList<>();
        for (String module : List.of("domain", "application", "infrastructure")) {
            String configured = System.getProperty("architecture." + module + ".classes");
            assertNotNull(configured, "Gradle must provide main outputs for " + module);
            List<Path> paths = Arrays.stream(configured.split(File.pathSeparator)).map(Path::of).toList();
            JavaClasses imported = importCompleteMainOutputs(paths);
            assertTrue(imported.stream().allMatch(c -> c.getPackageName().equals(ROOT + "." + module)
                            || c.getPackageName().startsWith(ROOT + "." + module + ".")),
                    "Unexpected package in " + module + " main output");
            directories.addAll(paths);
        }
        JavaClasses production = importCompleteMainOutputs(directories);
        assertFalse(production.contain(getClass()), "Tests must not be in the production import");
        rules(ROOT).forEach(rule -> rule.check(production));
    }

    @Test
    void missingOrEmptyOutputsCannotPass(@TempDir Path empty) {
        assertThrows(AssertionError.class, () -> importCompleteMainOutputs(List.of(empty.resolve("missing"))));
        assertThrows(AssertionError.class, () -> importCompleteMainOutputs(List.of(empty)));
        assertThrows(AssertionError.class, () -> importCompleteMainOutputs(List.of()));
    }

    @Test
    void invalidDependenciesAndLayerCycleAreRejected(@TempDir Path work) throws Exception {
        JavaClasses fixtures = compileFixtures(work, Map.of(
                "domain/Model", "public class Model { fixture.infrastructure.Adapter adapter; javax.sql.DataSource database; }",
                "application/UseCase", "public class UseCase { fixture.domain.Model model; fixture.infrastructure.Adapter adapter; }",
                "infrastructure/Adapter", "public class Adapter { fixture.application.UseCase useCase; fixture.Screen screen; }",
                "Screen", "public class Screen {}"));
        List<ArchRule> rules = rules("fixture");
        assertEquals(4, rules.size(), "Every production rule needs a negative control");
        for (ArchRule rule : rules) {
            AssertionError failure = assertThrows(AssertionError.class, () -> rule.check(fixtures), rule.getDescription());
            assertTrue(failure.getMessage().contains("fixture."), failure.getMessage());
        }
    }

    @Test
    void inwardDependenciesRemainAllowed(@TempDir Path work) throws Exception {
        JavaClasses fixtures = compileFixtures(work, Map.of(
                "domain/Model", "public class Model { java.time.Instant time; java.util.UUID id; }",
                "application/UseCase", "public class UseCase { fixture.domain.Model model; java.io.IOException failure; }",
                "infrastructure/Adapter", "public class Adapter { fixture.application.UseCase useCase; java.nio.file.Path file; }"));
        rules("fixture").forEach(rule -> rule.check(fixtures));
    }

    @Test
    void frameworkAndNetworkMechanismsCannotLeakInward(@TempDir Path work) throws Exception {
        JavaClasses fixtures = compileFixtures(work, Map.of(
                "domain/Model", "public class Model { javax.sql.DataSource database; }",
                "application/UseCase", "public class UseCase { java.net.Socket connection; }",
                "infrastructure/Adapter", "public class Adapter { fixture.Screen screen; }",
                "Screen", "public class Screen {}"));
        var rules = rules("fixture");
        for (int index = 0; index < 3; index++) {
            ArchRule rule = rules.get(index);
            assertThrows(AssertionError.class, () -> rule.check(fixtures), rule.getDescription());
        }
    }

    private static JavaClasses importCompleteMainOutputs(List<Path> paths) throws Exception {
        assertFalse(paths.isEmpty(), "No main output directories configured");
        Set<String> expected = new java.util.HashSet<>();
        for (Path path : paths) {
            assertTrue(Files.isDirectory(path), "Missing compiled main directory: " + path);
            try (var files = Files.walk(path)) {
                Set<String> names = files.filter(p -> p.toString().endsWith(".class"))
                        .map(path::relativize).map(Path::toString)
                        .map(p -> p.substring(0, p.length() - 6).replace(File.separatorChar, '.'))
                        .collect(Collectors.toSet());
                assertFalse(names.isEmpty(), "Empty compiled main directory: " + path);
                assertTrue(java.util.Collections.disjoint(expected, names), "Duplicate class across outputs: " + path);
                expected.addAll(names);
            }
        }
        JavaClasses imported = new ClassFileImporter().importPaths(paths);
        assertEquals(expected, imported.stream().map(c -> c.getName()).collect(Collectors.toSet()),
                "ArchUnit must import every main class, including nested classes");
        return imported;
    }

    private static JavaClasses compileFixtures(Path work, Map<String, String> sources) throws Exception {
        Path output = Files.createDirectories(work.resolve("classes"));
        List<String> arguments = new ArrayList<>(List.of("--release", "17", "-d", output.toString()));
        for (var entry : sources.entrySet()) {
            String name = entry.getKey();
            int slash = name.lastIndexOf('/');
            String packageName = slash < 0 ? "fixture" : "fixture." + name.substring(0, slash).replace('/', '.');
            Path source = work.resolve("src").resolve(name + ".java");
            Files.createDirectories(source.getParent());
            Files.writeString(source, "package " + packageName + ";\n" + entry.getValue());
            arguments.add(source.toString());
        }
        var compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler, "Architecture fixture tests require a JDK");
        assertEquals(0, compiler.run(null, null, null, arguments.toArray(String[]::new)), "Fixture compilation failed");
        return new ClassFileImporter().importPath(output);
    }

    private static List<ArchRule> rules(String root) {
        String domain = root + ".domain..";
        String application = root + ".application..";
        String infrastructure = root + ".infrastructure..";
        var domainDependencies = com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage(
                domain, "java.lang..", "java.util..", "java.time..", "java.math..", "java.text..");
        var applicationDependencies = domainDependencies.or(
                com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage(application))
                .or(DescribedPredicate.describe("URI value or IOException port failure", c ->
                        c.getName().equals("java.net.URI") || c.getName().equals("java.io.IOException")));
        return List.of(
                classes().that().resideInAPackage(domain).should().onlyDependOnClassesThat(domainDependencies)
                        .because("domain policy must remain independent of adapters and frameworks"),
                classes().that().resideInAPackage(application).should().onlyDependOnClassesThat(applicationDependencies)
                        .because("use cases depend on domain and JDK values, not adapter mechanisms"),
                classes().that().resideInAPackage(infrastructure).should().onlyDependOnClassesThat(
                        DescribedPredicate.describe("JVM adapters, inner layers or non-Android external libraries", c -> {
                            String name = c.getPackageName();
                            boolean projectClass = name.equals(root) || name.startsWith(root + ".");
                            boolean jvmLayer = List.of("domain", "application", "infrastructure").stream()
                                    .anyMatch(layer -> name.equals(root + "." + layer) || name.startsWith(root + "." + layer + "."));
                            return (!projectClass || jvmLayer) && !name.equals("android") && !name.startsWith("android.")
                                    && !name.equals("androidx") && !name.startsWith("androidx.");
                        })).because("JVM adapters must not depend on Android UI or Room"),
                slices().matching(root + ".(*)..").should().beFreeOfCycles()
                        .because("dependencies between JVM layers must be acyclic"));
    }
}
