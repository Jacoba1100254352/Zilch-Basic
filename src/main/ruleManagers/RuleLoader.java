package ruleManagers;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

final class RuleLoader
{
        private RuleLoader() {
        }

        static List<Rule> discoverRules(String basePackage) {
                try {
                        List<Class<? extends Rule>> classes = findRuleClasses(basePackage);
                        List<Rule> rules = new ArrayList<>();
                        for (Class<? extends Rule> clazz : classes) {
                                rules.add(clazz.getDeclaredConstructor().newInstance());
                        }
                        return rules;
                } catch (Exception e) {
                        throw new IllegalStateException("Failed to load rule implementations", e);
                }
        }

        private static List<Class<? extends Rule>> findRuleClasses(String basePackage)
                throws IOException, URISyntaxException, ClassNotFoundException {
                String path = basePackage.replace('.', '/');
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Enumeration<URL> resources = classLoader.getResources(path);
                List<Class<? extends Rule>> classes = new ArrayList<>();
                while (resources.hasMoreElements()) {
                        URL resource = resources.nextElement();
                        switch (resource.getProtocol()) {
                                case "file" -> classes.addAll(findRuleClassesInDirectory(basePackage, Path.of(resource.toURI())));
                                case "jar" -> classes.addAll(findRuleClassesInJar(basePackage, resource));
                                default -> {
                                        // Ignore unsupported protocols
                                }
                        }
                }
                return classes;
        }

        private static List<Class<? extends Rule>> findRuleClassesInDirectory(String basePackage, Path directory)
                throws IOException, ClassNotFoundException {
                List<Class<? extends Rule>> classes = new ArrayList<>();
                try (Stream<Path> walk = Files.walk(directory)) {
                        walk.filter(Files::isRegularFile)
                            .filter(path -> path.toString().endsWith(".class"))
                            .forEach(path -> {
                                    String relative = directory.relativize(path).toString();
                                    String className = basePackage + '.' + relative.replace('/', '.').replace('\\', '.')
                                                                           .replaceAll("\\.class$", "");
                                    try {
                                            addRuleClass(classes, className);
                                    } catch (ClassNotFoundException e) {
                                            throw new RuntimeException(e);
                                    }
                            });
                }
                return classes;
        }

        private static List<Class<? extends Rule>> findRuleClassesInJar(String basePackage, URL resource)
                throws IOException, ClassNotFoundException {
                List<Class<? extends Rule>> classes = new ArrayList<>();
                String resourcePath = resource.getPath();
                int separator = resourcePath.indexOf('!');
                if (separator == -1) {
                        return classes;
                }
                String jarPath = resourcePath.substring(5, separator);
                try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                        String packagePath = basePackage.replace('.', '/');
                        Enumeration<JarEntry> entries = jarFile.entries();
                        while (entries.hasMoreElements()) {
                                JarEntry entry = entries.nextElement();
                                if (entry.isDirectory()) {
                                        continue;
                                }
                                String name = entry.getName();
                                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                                        String className = name.replace('/', '.').replaceAll("\\.class$", "");
                                        addRuleClass(classes, className);
                                }
                        }
                }
                return classes;
        }

        @SuppressWarnings("unchecked")
        private static void addRuleClass(List<Class<? extends Rule>> classes, String className) throws ClassNotFoundException {
                Class<?> clazz = Class.forName(className);
                if (Rule.class.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                        classes.add((Class<? extends Rule>) clazz);
                }
        }
}
