package rules.managers;


import rules.variable.IRule;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Discovers compatible rule classes directly from the runtime classpath.
 * The scanner supports both exploded class directories and jar resources,
 * which lets the game auto-register rules without maintaining a provider list.
 */
public class RuleScanner
{
	/**
	 * Finds and instantiates non-abstract {@link IRule} implementations under
	 * the supplied package.
	 *
	 * @param basePackage The package root to scan.
	 * @return Sorted rule instances discovered on the classpath.
	 */
	public List<IRule> discoverRules(String basePackage) {
		String packagePath = basePackage.replace('.', '/');
		Set<String> classNames = new TreeSet<>();

		try {
			Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				if ("file".equals(resource.getProtocol())) {
					scanDirectory(Path.of(URI.create(resource.toString())), basePackage, classNames);
				} else if ("jar".equals(resource.getProtocol())) {
					scanJar(resource, packagePath, classNames);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException("Unable to discover rule classes.", e);
		}

		List<IRule> rules = new ArrayList<>();
		for (String className : classNames) {
			IRule rule = instantiateRule(className);
			if (rule != null) {
				rules.add(rule);
			}
		}

		rules.sort(Comparator.comparing(IRule::getDisplayName));
		return rules;
	}

	/**
	 * Recursively walks a package directory and records concrete class names.
	 */
	private void scanDirectory(Path directory, String packageName, Set<String> classNames) throws IOException {
		try (var paths = Files.list(directory)) {
			for (Path path : paths.toList()) {
				String fileName = path.getFileName().toString();
				if (Files.isDirectory(path)) {
					scanDirectory(path, packageName + "." + fileName, classNames);
				} else if (fileName.endsWith(".class") && !fileName.contains("$")) {
					classNames.add(packageName + "." + fileName.substring(0, fileName.length() - 6));
				}
			}
		}
	}

	/**
	 * Scans rule classes packaged inside a jar.
	 */
	private void scanJar(URL resource, String packagePath, Set<String> classNames) throws IOException {
		JarURLConnection connection = (JarURLConnection) resource.openConnection();
		try (JarFile jarFile = connection.getJarFile()) {
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				if (name.startsWith(packagePath) && name.endsWith(".class") && !name.contains("$")) {
					classNames.add(name.substring(0, name.length() - 6).replace('/', '.'));
				}
			}
		}
	}

	/**
	 * Instantiates a discovered rule class through its no-argument constructor.
	 */
	private IRule instantiateRule(String className) {
		try {
			Class<?> clazz = Class.forName(className);
			if (!IRule.class.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
				return null;
			}

			Constructor<?> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (IRule) constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Unable to instantiate rule class: " + className, e);
		}
	}
}
