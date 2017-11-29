package io.inkstudios.spigot.lock;

import org.bukkit.plugin.java.JavaPlugin;

public final class LockPlugin extends JavaPlugin {
	
	private static LockPlugin instance;
	
	public static LockPlugin getInstance() {
		return LockPlugin.instance;
	}
	
	@Override
	public void onEnable() {
		LockPlugin.instance = this;
	}
	
	@Override
	public void onDisable() {
		LockPlugin.instance = null;
	}
	
}
