package ruleManagers.rules;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class RuleLoader {
    private RuleLoader() {
    }

    public static List<Rule> loadRules(String basePackage) {
        String packagePath = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = RuleLoader.class.getClassLoader();
        }

        List<Rule> rules = new ArrayList<>();
        Set<String> discovered = new HashSet<>();

        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    loadFromFileSystem(basePackage, Paths.get(resource.toURI()), rules, discovered);
                } else if ("jar".equals(protocol)) {
                    loadFromJar(resource, rules, discovered);
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Unable to scan package for rules: " + basePackage, e);
        }

        return rules;
    }

    private static void loadFromFileSystem(String basePackage, Path directory, List<Rule> rules, Set<String> discovered) {
        if (!Files.isDirectory(directory)) {
            return;
        }
        try {
            Files.walk(directory)
                 .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".class"))
                 .forEach(path -> {
                     String className = basePackage + '.' + directory.relativize(path)
                             .toString()
                             .replace('/', '.')
                             .replace('\\', '.')
                             .replaceAll("\\.class$", "");
                     addRule(className, rules, discovered);
                 });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void loadFromJar(URL resource, List<Rule> rules, Set<String> discovered) throws IOException {
        JarURLConnection connection = (JarURLConnection) resource.openConnection();
        try (JarFile jarFile = connection.getJarFile()) {
            String packagePrefix = connection.getEntryName();
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(packagePrefix) && name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.replace('/', '.').replaceAll("\\.class$", "");
                    addRule(className, rules, discovered);
                }
            }
        }
    }

    private static void addRule(String className, List<Rule> rules, Set<String> discovered) {
        if (className.contains("$")) {
            return; // skip inner classes
        }
        if (!discovered.add(className)) {
            return;
        }
        try {
            Class<?> clazz = Class.forName(className);
            if (!Rule.class.isAssignableFrom(clazz)) {
                return;
            }
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                return;
            }
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Rule rule = (Rule) constructor.newInstance();
            rules.add(rule);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate rule " + className, e);
        }
    }
}
