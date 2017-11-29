package io.inkstudios.spigot.lock;

import io.inkstudios.spigot.lock.database.LockMySQLDatabase;

import org.bukkit.plugin.java.JavaPlugin;

public final class LockPlugin extends JavaPlugin {
	
	private static LockPlugin instance;
	
	public static LockPlugin getInstance() {
		return LockPlugin.instance;
	}
	
	private LockMySQLDatabase sqlDatabase;
	
	@Override
	public void onEnable() {
		LockPlugin.instance = this;
		this.loadSettings();
	}
	
	@Override
	public void onDisable() {
		LockPlugin.instance = null;
	}
	
	private void loadSettings() {
		this.getConfig().options().copyDefaults(true);
		this.saveDefaultConfig();
		this.loadSqlDatabase();
	}
	
	private void loadSqlDatabase() {
		this.sqlDatabase = new LockMySQLDatabase(this.getConfig());
		this.sqlDatabase.createDefaultTables();
	}
	
	public LockMySQLDatabase getSqlDatabase() {
		return this.sqlDatabase;
	}
	
}
