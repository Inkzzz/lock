package io.inkstudios.spigot.lock;

import io.inkstudios.spigot.lock.account.AccountListener;
import io.inkstudios.spigot.lock.account.AccountRegistry;
import io.inkstudios.spigot.lock.account.AccountType;
import io.inkstudios.spigot.lock.account.LockAccount;
import io.inkstudios.spigot.lock.command.LockCommand;
import io.inkstudios.spigot.lock.command.LocksCommand;
import io.inkstudios.spigot.lock.command.UnlockCommand;
import io.inkstudios.spigot.lock.database.LockMySQLDatabase;

import org.bukkit.plugin.java.JavaPlugin;

public final class LockPlugin extends JavaPlugin {
	
	private static LockPlugin instance;
	
	public static LockPlugin getInstance() {
		return LockPlugin.instance;
	}
	
	private AccountRegistry accountRegistry;
	private LockMySQLDatabase sqlDatabase;
	
	@Override
	public void onEnable() {
		LockPlugin.instance = this;
		this.loadSettings();
		
		this.getCommand("locks").setExecutor(new LocksCommand());
		this.getCommand("lock").setExecutor(new LockCommand());
		this.getCommand("unlock").setExecutor(new UnlockCommand());
		
		this.getServer().getPluginManager().registerEvents(new AccountListener(), this);
		this.getServer().getPluginManager().registerEvents(new LockListener(), this);
	}
	
	@Override
	public void onDisable() {
		this.accountRegistry.streamAccounts().forEach(LockAccount::save);
		
		LockPlugin.instance = null;
	}
	
	private void loadSettings() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		AccountType accountType = AccountType
				.getAccountTypeOrPersistent(this.getConfig().getString("account-type"));
		
		this.accountRegistry = new AccountRegistry(accountType);
		this.accountRegistry.loadPlayerData();
		
		if (accountType == AccountType.MYSQL) {
			this.loadSqlDatabase();
		}
	}
	
	private void loadSqlDatabase() {
		this.sqlDatabase = new LockMySQLDatabase(this.getConfig());
		this.sqlDatabase.createDefaultTables();
	}
	
	/**
	 * @return the {@link AccountRegistry} instance
	 */
	public AccountRegistry getAccountRegistry() {
		return this.accountRegistry;
	}
	
	/**
	 * <p>This instance will be null if {@link AccountType} does not equal {@link AccountType#MYSQL}</p>
	 *
	 * @return the {@link LockMySQLDatabase} instance
	 */
	public LockMySQLDatabase getSqlDatabase() {
		return this.sqlDatabase;
	}
	
}
