package ruleManagers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * Discovers available {@link RuleStrategy RuleStrategies} on the classpath.
 */
public class RuleRegistry
{
        private static final String DEFAULT_RULE_PACKAGE = "ruleManagers.rules";

        private final Map<String, RuleStrategy> strategies;

        public RuleRegistry()
        {
                this.strategies = discoverStrategies();
        }

        public Collection<RuleStrategy> strategies()
        {
                return strategies.values();
        }

        public RuleStrategy findById(String ruleId)
        {
                return strategies.get(ruleId);
        }

        private Map<String, RuleStrategy> discoverStrategies()
        {
                Map<String, RuleStrategy> discovered = new LinkedHashMap<>();
                loadFromServiceLoader(discovered);
                loadFromPackage(discovered, DEFAULT_RULE_PACKAGE);
                return discovered;
        }

        private void loadFromServiceLoader(Map<String, RuleStrategy> discovered)
        {
                ServiceLoader.load(RuleStrategy.class).forEach(strategy -> register(discovered, strategy));
        }

        private void loadFromPackage(Map<String, RuleStrategy> discovered, String packageName)
        {
                String path = packageName.replace('.', '/');
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                try {
                        Enumeration<URL> resources = classLoader.getResources(path);
                        while (resources.hasMoreElements()) {
                                URL resource = resources.nextElement();
                                if (!Objects.equals(resource.getProtocol(), "file")) {
                                        continue;
                                }
                                try {
                                        Path directory = Paths.get(resource.toURI());
                                        if (!Files.isDirectory(directory)) {
                                                continue;
                                        }
                                        try (var stream = Files.list(directory)) {
                                                stream.filter(file -> file.getFileName().toString().endsWith(".class"))
                                                      .filter(file -> !file.getFileName().toString().contains("$"))
                                                      .sorted()
                                                      .forEach(file -> loadClass(discovered, packageName, file.getFileName()
                                                                                                              .toString()));
                                        }
                                } catch (URISyntaxException | IOException ignored) {
                                        // Swallow exceptions and continue discovery. Rules can still be loaded via ServiceLoader.
                                }
                        }
                } catch (IOException ignored) {
                        // No additional classes to discover.
                }
        }

        private void loadClass(Map<String, RuleStrategy> discovered, String packageName, String classFileName)
        {
                String simpleName = classFileName.substring(0, classFileName.length() - ".class".length());
                String className = packageName + '.' + simpleName;
                try {
                        Class<?> candidate = Class.forName(className);
                        if (!RuleStrategy.class.isAssignableFrom(candidate)) {
                                return;
                        }
                        if (candidate.isInterface() || java.lang.reflect.Modifier.isAbstract(candidate.getModifiers())) {
                                return;
                        }
                        RuleStrategy strategy = (RuleStrategy) candidate.getDeclaredConstructor().newInstance();
                        register(discovered, strategy);
                } catch (ReflectiveOperationException ignored) {
                        // Ignore classes that cannot be instantiated and continue discovery.
                }
        }

        private void register(Map<String, RuleStrategy> discovered, RuleStrategy strategy)
        {
                discovered.putIfAbsent(strategy.id(), strategy);
        }

        public List<RuleDescriptor> descriptors()
        {
                List<RuleDescriptor> descriptors = new ArrayList<>();
                strategies.values()
                          .stream()
                          .sorted(Comparator.comparingInt(RuleStrategy::priority).thenComparing(RuleStrategy::displayName))
                          .forEach(strategy -> descriptors.add(new RuleDescriptor(strategy.id(), strategy.displayName(),
                                                                                  strategy.description())));
                return descriptors;
        }
}
