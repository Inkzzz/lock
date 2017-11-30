package io.inkstudios.spigot.lock.data;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * {@link YamlFile} predicate to check if the path of the file is a yaml file.
 *
 * <p>The YAML file must have the right extensions (known as being one of the following: .yml | .yaml)</p>
 *
 * <p>{@link YamlFile} is an Enum type to prevent further instances being instantiated without reflection</p>
 */
public enum YamlFile implements Predicate<Path> {
	
	INSTANCE;
	
	@Override
	public boolean test(Path path) {
		String fileName = path.toString();
		return fileName.endsWith(".yml") || fileName.endsWith(".yaml");
	}
	
}
