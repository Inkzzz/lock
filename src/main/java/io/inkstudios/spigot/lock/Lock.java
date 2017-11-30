package io.inkstudios.spigot.lock;

import org.bukkit.Location;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class Lock {
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		
		private int id;
		private UUID owner;
		private final Set<Location> lockLocations = new HashSet<>();
		
		private Builder() {
			
		}
		
		public Lock build() {
			Objects.requireNonNull(this.owner, "owner");
			return new Lock(this.id, this.owner, this.lockLocations);
		}
		
		public Builder setId(int id) {
			this.id = id;
			return this;
		}
		
		public Builder setOwner(UUID owner) {
			this.owner = owner;
			return this;
		}
		
		public Builder addLocation(Location location) {
			Objects.requireNonNull(location, "location");
			this.lockLocations.add(location);
			return this;
		}
		
		public Builder addLocations(Location... locations) {
			Arrays.stream(locations).forEach(this::addLocation);
			return this;
		}
		
		public Builder addLocations(Collection<Location> locations) {
			locations.forEach(this::addLocation);
			return this;
		}
		
	}
	
	private int id;
	private final UUID owner;
	private final Set<Location> lockLocations = new HashSet<>();
	
	private Lock(int id, UUID owner, Set<Location> lockLocations) {
		this.id = id;
		this.owner = owner;
		this.lockLocations.addAll(lockLocations);
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public UUID getOwner() {
		return this.owner;
	}
	
	public boolean isLockLocation(Location location) {
		return this.lockLocations.contains(location);
	}
	
	public Set<Location> getLockLocations() {
		return Collections.unmodifiableSet(this.lockLocations);
	}
	
}
