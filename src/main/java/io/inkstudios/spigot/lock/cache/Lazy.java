package io.inkstudios.spigot.lock.cache;

import java.util.function.Supplier;

/**
 * A cache feature to hold data or code to be executed when required, rather than beforehand.
 *
 * <p>Lazy uses a supplier to allow mass amount of data to be surpassed.</p>
 *
 * @param <T> the generic type of {@link Lazy}
 */
public final class Lazy<T> {
	
	private final Supplier<T> supplier;
	private T value;
	private boolean found;
	
	/**
	 * Constructs a new {@link Lazy} instance
	 *
	 * @param supplier the supplier of lazy
	 */
	private Lazy(Supplier<T> supplier) {
		this.supplier = supplier;
	}
	
	/**
	 * Creates a new Lazy instance with the desired supplier
	 * @param supplier the supplier of lazy
	 * @param <T> the generic type of lazy
	 * @return a new lazy instance with the desired supplier {@code supplier}
	 */
	public static <T> Lazy<T> of(Supplier<T> supplier) {
		return new Lazy<>(supplier);
	}
	
	/**
	 * Gets the value of {@link Lazy} through the supplier {@link #supplier}
	 * @return the value being held
	 */
	public T get() {
		if (this.found) {
			return this.value;
		}
		
		this.found = true;
		this.value = this.supplier.get();
		return this.value;
	}
	
}
