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
	
	public AccountRegistry(AccountType accountType) {
		this.accountType = accountType;
	}
	
	public void loadPlayerData() {
		if (this.accountType != AccountType.PERSISTENT) {
			return;
		}
		
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
	
	private File getDataFile(UUID uniqueId) {
		Path path = this.getDataFile(uniqueId.toString());
		
		if (path == null) {
			throw new AccountException("Failed to get file for " + uniqueId.toString());
		}
		
		return path.toFile();
	}
	
	private Path getDataFile(String pointer) {
		Path file = this.lazyPlayerDataPath.get().resolve(pointer + ".yml");
		
		if (!this.validateFile(file)) {
			try {
				Files.createDirectories(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
	private boolean validateFile(Path file)
	{
		if (Files.exists(file))
		{
			if (Files.isRegularFile(file))
			{
				return true;
			}
			
			throw new IllegalStateException(file + " must be a regular file");
		}
		
		return false;
	}
	
	private String resolveName(Path path) {
		String name = path.getFileName().toString();
		
		int lastIndex = name.indexOf('.');
		if (lastIndex == -1) {
			return name;
		}
		
		return name.substring(0, lastIndex);
	}
	
	public LockAccount getAccount(UUID uniqueId) {
		return this.lockAccounts.computeIfAbsent(uniqueId, this::createAccount);
	}
	
	public void invalidateAccount(UUID uniqueId) {
		this.lockAccounts.remove(uniqueId);
	}
	
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
	
	public Stream<LockAccount> streamAccounts() {
		return this.lockAccounts.values().stream();
	}
	
}
