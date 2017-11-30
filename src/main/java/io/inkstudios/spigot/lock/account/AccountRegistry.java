package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.LockPlugin;
import io.inkstudios.spigot.lock.cache.Lazy;
import io.inkstudios.spigot.lock.data.YamlFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public final class AccountRegistry {
	
	private final Map<UUID, LockAccount> lockAccounts = new HashMap<>();
	private final Lazy<Path> lazyPlayerDataPath =
			Lazy.of(() -> LockPlugin.getInstance().getDataFolder().toPath().resolve("players"));
	
	private final AccountType accountType;
	
	/**
	 * Constructs an {@link AccountRegistry} of the specified {@link AccountRegistry} {@code accountType}
	 *
	 * @param accountType the account type of the registry
	 */
	public AccountRegistry(AccountType accountType) {
		this.accountType = accountType;
	}
	
	/**
	 * Loads player data
	 *
	 * <p>Only loads player data if the account type is persistent</p>
	 */
	public void loadPlayerData() {
		if (this.accountType != AccountType.PERSISTENT) {
			return;
		}
		
		this.createPlayerDataFolderIfNotExists();
		
		Path file = this.lazyPlayerDataPath.get();
		
		file.forEach(path -> {
			if (!YamlFile.INSTANCE.test(path)) {
				return;
			}
			
			String name = this.resolveName(path);
			
			UUID uniqueId = UUID.fromString(name);
			
			PersistentLockAccount lockAccount = new PersistentLockAccount(uniqueId, path.toFile());
			lockAccount.loadDataIfPresent();
			
			this.lockAccounts.put(uniqueId, lockAccount);
		});
	}
	
	/**
	 * Creates the players directory within dir 'plugins/locks'
	 */
	private void createPlayerDataFolderIfNotExists() {
		Path path = this.lazyPlayerDataPath.get();
		
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the yaml file related to the unique id {@code uniqueId}
	 *
	 * @param uniqueId the owner of the file
	 * @return the file related to {@code uniqueId}
	 */
	private File getDataFile(UUID uniqueId) {
		return this.getDataFile(this.lazyPlayerDataPath.get(), uniqueId.toString());
	}
	
	/**
	 * Gets a yaml file within path {@code path} with the name {@code pointer}
	 *
	 * <p>Attempts to create the file if it doesn't exists</p>
	 *
	 * @param path the path to the file
	 * @param pointer the name of the file
	 * @return
	 */
	private File getDataFile(Path path, String pointer) {
		File file = path.resolve(pointer + ".yml").toFile();
	
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
	/**
	 * Resolves the name of a path
	 *
	 * <p>The extension of the file is excluded</p>
	 *
	 * @param path the path to resolve the name of
	 * @return the resolved name
	 */
	private String resolveName(Path path) {
		String name = path.getFileName().toString();
		
		int lastIndex = name.indexOf('.');
		if (lastIndex == -1) {
			return name;
		}
		
		return name.substring(0, lastIndex);
	}
	
	/**
	 * Returns the {@link LockAccount} related to {@code uniqueId}
	 *
	 * @param uniqueId the uniqueId of the account to fetch
	 * @return the lock account related to the uniqueId
	 */
	public LockAccount getAccount(UUID uniqueId) {
		return this.lockAccounts.computeIfAbsent(uniqueId, this::createAccount);
	}
	
	/**
	 * Invalidates the {@link LockAccount} related to the unique id {@code uniqueId}
	 *
	 * @param uniqueId the unique id of the account to invalidate
	 */
	public void invalidateAccount(UUID uniqueId) {
		this.lockAccounts.remove(uniqueId);
	}
	
	/**
	 * Creates a {@link LockAccount} related to the unique id {@code uniqueId}
	 *
	 * @param uniqueId the unique id related to the account
	 * @return a new {@link LockAccount} instance, depending on the configuration
	 */
	private LockAccount createAccount(UUID uniqueId) {
		switch (this.accountType) {
			case PERSISTENT:
				return new PersistentLockAccount(uniqueId, this.getDataFile(uniqueId));
				
			case MYSQL:
				return new SQLLockAccount(uniqueId);
				
			default:
				throw new UnsupportedOperationException("No support implemented for account type, "
						+ this.accountType.getName());
		}
	}
	
	/**
	 * @return a stream of all registered {@link LockAccount}'s
	 */
	public Stream<LockAccount> streamAccounts() {
		return this.lockAccounts.values().stream();
	}
	
}
