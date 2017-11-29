package io.inkstudios.spigot.lock;

import io.inkstudios.spigot.lock.account.AccountRegistry;
import io.inkstudios.spigot.lock.account.LockAccount;
import io.inkstudios.spigot.lock.cache.Lazy;
import io.inkstudios.spigot.lock.util.ChatUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public class LockListener implements Listener {
	
	private static final Set<Material> LOCK_MATERIALS = EnumSet.of(Material.CHEST, Material.ENDER_CHEST,
			Material.ANVIL, Material.FURNACE, Material.BURNING_FURNACE, Material.ACACIA_DOOR, Material.BIRCH_DOOR,
			Material.DARK_OAK_DOOR, Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR,
			Material.SPRUCE_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR);
	
	private final Lazy<AccountRegistry> registryLazy = Lazy.of(() -> LockPlugin.getInstance().getAccountRegistry());
	
	@EventHandler
	private void on(PlayerInteractEvent event) {
		Action action = event.getAction();
		
		if (action != Action.RIGHT_CLICK_BLOCK && action != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (!LockListener.LOCK_MATERIALS.contains(block.getType())) {
			return;
		}
		
		Lock lockFound = this.findLock(block.getLocation());
		
		LockAccount account = this.registryLazy.get().getAccount(player.getUniqueId());
		
		if (lockFound != null) {
			if (!lockFound.getOwner().equals(player.getUniqueId())) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Event.Result.DENY);
				player.sendMessage(ChatUtil.toColour("&cThis object is locked."));
			}
			
			LockState state = account.getState();
			
			if (state != null && state == LockState.UNLOCK) {
				account.removeLock(lockFound);
				account.setState(null);
				
				player.sendMessage(ChatUtil.toColour("&cYou have removed your lock on this object."));
			}
			
			return;
		}
		
		LockState state = account.getState();
		
		if (state == null || state != LockState.LOCK) {
			return;
		}
		
		Lock.Builder lockBuilder = Lock.builder()
				.setOwner(player.getUniqueId())
				.addLocation(block.getLocation());
		
		Location secondChest = this.findSecondChest(block);
		
		if (secondChest != null) {
			lockBuilder.addLocation(secondChest);
		}
		
		account.addLock(lockBuilder.build());
		account.setState(null);
		
		player.sendMessage(ChatUtil.toColour("&cYou have locked this object."));
	}
	
	private Lock findLock(Location location) {
		return this.registryLazy.get().streamAccounts()
				.map(account -> account.getLock(location))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.orElse(null);
	}
	
	private Location findSecondChest(Block block) {
		Block current = block.getRelative(0, 0, -1);
		
		if (this.isDoubleChestType(current)) {
			return current.getLocation();
		}
		
		current = block.getRelative(1, 0, 0);
		
		if (this.isDoubleChestType(current)) {
			return current.getLocation();
		}
		
		current = block.getRelative(0, 0, 1);
		
		if (this.isDoubleChestType(current)) {
			return current.getLocation();
		}
		
		current = block.getRelative(-1, 0, 0);
		
		if (this.isDoubleChestType(current)) {
			return current.getLocation();
		}
		
		return null;
	}
	
	private boolean isDoubleChestType(Block block) {
		Material type = block.getType();
		return type == Material.CHEST || type == Material.TRAPPED_CHEST;
	}
	
}
