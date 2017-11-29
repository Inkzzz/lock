package io.inkstudios.spigot.lock.cache;

import java.util.function.Supplier;

public final class Lazy<T> {
	
	private final Supplier<T> supplier;
	private T value;
	private boolean found;
	
	private Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}
	
	public T get() {
		if (this.found) {
			return this.value;
		}
		
		this.found = true;
		this.value = this.supplier.get();
		return this.value;
	}
	
}
