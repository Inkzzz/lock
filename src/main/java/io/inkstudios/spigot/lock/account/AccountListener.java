package io.inkstudios.spigot.lock.account;

import io.inkstudios.spigot.lock.LockPlugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class AccountListener implements Listener {
	
	@EventHandler
	private void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		LockPlugin.getInstance().getAccountRegistry().getAccount(player.getUniqueId()).loadDataIfPresent();
	}
	
	@EventHandler
	private void on(PlayerQuitEvent event) {
		UUID uniqueId = event.getPlayer().getUniqueId();
		
		LockPlugin plugin = LockPlugin.getInstance();
		
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			AccountRegistry registry = plugin.getAccountRegistry();
			
			LockAccount account = registry.getAccount(uniqueId);
			account.save();
			
			if (!(account instanceof PersistentLockAccount)) {
				registry.invalidateAccount(uniqueId);
			}
		});
	}
	
}
