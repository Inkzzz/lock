package io.inkstudios.spigot.lock.data;

import java.nio.file.Path;
import java.util.function.Predicate;

public enum YamlFile implements Predicate<Path> {
	
	INSTANCE;
	
	@Override
	public boolean test(Path path) {
		String fileName = path.toString();
		return fileName.endsWith(".yml") || fileName.endsWith(".yaml");
	}
	
}
