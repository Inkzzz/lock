package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.Lock;
import io.inkstudios.spigot.lock.util.LocationUtil;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

final class PersistentLockAccount extends SimpleLockAccount {
	
	private final Object lock = new Object();
	private final File file;
	private final FileConfiguration configuration;
	
	/**
	 * Constructs a new {@link PersistentLockAccount} instance
	 * @param uniqueId the owner of the account
	 * @param locks the pre-set locks of the account
	 * @param file the file of the account
	 */
	public PersistentLockAccount(UUID uniqueId, List<Lock> locks, File file) {
		super(uniqueId, locks);
		
		this.file = file;
		this.configuration = YamlConfiguration.loadConfiguration(file);
	}
	
	/**
	 * Constructs a new {@link PersistentLockAccount} instance
	 * @param uniqueId the owner of the account
	 * @param file the file of the account
	 */
	public PersistentLockAccount(UUID uniqueId, File file) {
		super(uniqueId);
		
		this.file = file;
		this.configuration = YamlConfiguration.loadConfiguration(file);
	}
	
	@Override
	public void loadDataIfPresent() {
		if (!this.configuration.contains("locks")) {
			return;
		}
		
		this.configuration.getConfigurationSection("locks").getKeys(false).forEach(key -> {
			String path = "locks." + key + ".";
			
			Lock.Builder builder = Lock.builder()
					.setOwner(this.getUniqueId());
			
			this.configuration.getStringList(path + "locations")
					.forEach(stringLoc -> builder.addLocation(LocationUtil.fromString(stringLoc)));
			
			this.addLockWithoutSaving(builder.build());
		});
	}
	
	@Override
	public void save() {
		if (!this.isMarkedForWrite()) {
			return;
		}
		
		synchronized (this.lock) {
			this.setLocksToFile();
			
			try {
				this.configuration.save(this.file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			this.unmarkForWrite();
		}
	}
	
	/**
	 * Removes all locks from the file, and then sets all the current locks
	 */
	private void setLocksToFile() {
		this.configuration.set("locks", null);
		
		int counter = 0;
		
		for (Lock lock : this.getLocks()) {
			String path = "locks." + counter + ".";
			
			this.configuration.set(path + "locations",
					lock.getLockLocations().stream().map(LocationUtil::toString).collect(Collectors.toList()));
			
			counter++;
		}
	}
	
	@Override
	void removedLock(Lock lock) {
		// ignore as not required for flat files
	}
	
}
