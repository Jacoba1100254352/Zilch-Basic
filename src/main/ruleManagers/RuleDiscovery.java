package ruleManagers;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Discovers all {@link Rule} implementations that live in the {@code ruleManagers.rules} package.
 * This keeps the game flexible: dropping in a new rule class automatically makes it available.
 */
final class RuleDiscovery
{
        private static final String RULES_PACKAGE = "ruleManagers.rules";

        private RuleDiscovery() {}

        static List<Rule> discoverRules() {
                List<Rule> rules = new ArrayList<>();
                String path = RULES_PACKAGE.replace('.', '/');
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                try {
                        Enumeration<URL> resources = classLoader.getResources(path);
                        while (resources.hasMoreElements()) {
                                URL resource = resources.nextElement();
                                if (!"file".equals(resource.getProtocol())) {
                                        continue;
                                }
                                Path directory = Paths.get(resource.toURI());
                                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.class")) {
                                        for (Path entry : stream) {
                                                String fileName = entry.getFileName().toString();
                                                if (fileName.contains("$")) {
                                                        continue; // Skip inner classes
                                                }
                                                String className = RULES_PACKAGE + '.' + fileName.substring(0, fileName.length() - 6);
                                                Class<?> clazz = Class.forName(className);
                                                if (!Rule.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
                                                        continue;
                                                }
                                                Constructor<?> constructor = clazz.getDeclaredConstructor();
                                                constructor.setAccessible(true);
                                                rules.add((Rule) constructor.newInstance());
                                        }
                                }
                        }
                } catch (IOException | URISyntaxException e) {
                        throw new IllegalStateException("Failed to access rule definitions", e);
                } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException("Failed to instantiate rule", e);
                }

                return rules;
        }
}
