package io.inkstudios.spigot.lock.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public final class LocationUtil {
	
	private static final Pattern LOCATION_SPLITTER = Pattern.compile(",");
	
	private LocationUtil() {
		throw new IllegalStateException("Cannot create an instance of " + this.getClass().getSimpleName());
	}
	
	/**
	 * Serializes the specified location {@code location}
	 *
	 * <p>Each location element will be separated by LOCATION_SPLITTER</p>
	 *
	 * @param location location to serialize
	 * @return serialized location
	 */
	public static String toString(Location location) {
		Objects.requireNonNull(location, "location");
		
		StringJoiner joiner = new StringJoiner(LocationUtil.LOCATION_SPLITTER.pattern());
		joiner.add(location.getWorld().getName());
		joiner.add(String.valueOf(location.getX()));
		joiner.add(String.valueOf(location.getY()));
		joiner.add(String.valueOf(location.getZ()));
		joiner.add(String.valueOf(location.getYaw()));
		joiner.add(String.valueOf(location.getPitch()));
		
		return joiner.toString();
	}
	
	/**
	 * Deserializers a string to a {@link Location}
	 *
	 * @param serializedLocation the serialized location
	 * @return a location from {@code serializedLocation}
	 * @throws IndexOutOfBoundsException if there aren't enough array elements when the {@code serializedLocation}
	 * is split up by LOCATION_SPLITTER
	 * @throws NullPointerException if the deserialize world does not exists
	 */
	public static Location fromString(String serializedLocation) {
		Objects.requireNonNull(serializedLocation, "content");
		
		String[] sections = LocationUtil.LOCATION_SPLITTER.split(serializedLocation);
		
		if (sections.length < 6) {
			throw new IndexOutOfBoundsException("There are less than six components of " +
					"the serialized location string array");
		}
		
		World world = Bukkit.getWorld(sections[0]);
		
		if (world == null) {
			throw new NullPointerException("Could not find world named, " + sections[0]);
		}
		
		double x = Double.valueOf(sections[1]),
				y = Double.valueOf(sections[2]),
				z = Double.valueOf(sections[3]);
		
		float yaw = Float.valueOf(sections[4]),
				pitch = Float.valueOf(sections[5]);
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
}
