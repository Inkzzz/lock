package io.inkstudios.spigot.lock;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * {@link Lock} object holds the data of a block type, preventing other users accessing it without permission.
 */
public final class Lock {
	
	/**
	 * @return a new empty instance of {@link Lock.Builder}
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private int id;
		private UUID owner;
		private final Set<Location> lockLocations = new HashSet<>();
		
		private Builder() {
			
		}
		
		/**
		 * Creates a new instance of {@link Lock} with the elements configured within the builder.
		 *
		 * @return the new {@link Lock} instance
		 * @throws NullPointerException if {@link #owner} is null
		 */
		public Lock build() {
			Objects.requireNonNull(this.owner, "owner");
			return new Lock(this.id, this.owner, this.lockLocations);
		}
		
		/**
		 * Sets the lock builder id
		 * @param id the new id
		 * @return this
		 */
		public Builder setId(int id) {
			this.id = id;
			return this;
		}
		
		/**
		 * Sets the lock builder owner
		 * @param owner the owners unique id
		 * @return this
		 */
		public Builder setOwner(UUID owner) {
			this.owner = owner;
			return this;
		}
		
		/**
		 * Adds a lock builder location
		 * @param location a location of lock
		 * @return this
		 */
		public Builder addLocation(Location location) {
			Objects.requireNonNull(location, "location");
			this.lockLocations.add(location);
			return this;
		}
		
		/**
		 * Adds lock builder locations
		 * @param locations array of locations abundant to a lock
		 * @return this
		 */
		public Builder addLocations(Location... locations) {
			Arrays.stream(locations).forEach(this::addLocation);
			return this;
		}
		
		/**
		 * Adds lock builder locations
		 * @param locations collection of locations abundant to a lock
		 * @return this
		 */
		public Builder addLocations(Collection<Location> locations) {
			locations.forEach(this::addLocation);
			return this;
		}
		
	}
	
	private int id;
	private final UUID owner;
	private final Set<Location> lockLocations = new HashSet<>();
	
	/**
	 * Constructs a new {@link Lock} instance
	 *
	 * @param id the id of the lock
	 * @param owner the owner of the lock (players unique id)
	 * @param lockLocations - a collection of locations abundant to the lock
	 */
	private Lock(int id, UUID owner, Set<Location> lockLocations) {
		this.id = id;
		this.owner = owner;
		this.lockLocations.addAll(lockLocations);
	}
	
	/**
	 * <p>This is set at -1 by default, but will be set by the database if the
	 * {@link io.inkstudios.spigot.lock.account.AccountType} being used is SQL</p>
	 *
	 * @return the lock id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the id of the lock
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return the unique id of the owner of this lock
	 */
	public UUID getOwner() {
		return this.owner;
	}
	
	/**
	 * Checks if the location {@code location} specified is abundant to this lock
	 *
	 * @param location the location to check
	 * @return true if the {@code location} is abundant to this lock
	 */
	public boolean isLockLocation(Location location) {
		return this.lockLocations.contains(location);
	}
	
	/**
	 * @return all of the locations abundant to this lock
	 */
	public Set<Location> getLockLocations() {
		return Collections.unmodifiableSet(this.lockLocations);
	}
	
}
