package io.inkstudios.spigot.lock;

import io.inkstudios.spigot.lock.account.AccountListener;
import io.inkstudios.spigot.lock.account.AccountRegistry;
import io.inkstudios.spigot.lock.account.AccountType;
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
		
		this.getServer().getPluginManager().registerEvents(new AccountListener(), this);
	}
	
	@Override
	public void onDisable() {
		LockPlugin.instance = null;
	}
	
	private void loadSettings() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		
		this.accountRegistry = new AccountRegistry(AccountType
				.getAccountTypeOrPersistent(this.getConfig().getString("account-type")));
		this.accountRegistry.loadPlayerData();
		
		this.loadSqlDatabase();
	}
	
	private void loadSqlDatabase() {
		this.sqlDatabase = new LockMySQLDatabase(this.getConfig());
		this.sqlDatabase.createDefaultTables();
	}
	
	public AccountRegistry getAccountRegistry() {
		return this.accountRegistry;
	}
	
	public LockMySQLDatabase getSqlDatabase() {
		return this.sqlDatabase;
	}
	
}
