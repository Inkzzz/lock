package io.inkstudios.spigot.lock.command;

import io.inkstudios.spigot.lock.LockPlugin;
import io.inkstudios.spigot.lock.LockState;
import io.inkstudios.spigot.lock.account.LockAccount;
import io.inkstudios.spigot.lock.util.ChatUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /unlock command
 */
public class UnlockCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		
		Player player = (Player) sender;
		
		LockAccount account = LockPlugin.getInstance().getAccountRegistry().getAccount(player.getUniqueId());
		
		LockState state = account.getState();
		
		if (state != null && state == LockState.UNLOCK) {
			account.setState(null);
			player.sendMessage(ChatUtil.toColour("&aYou have removed your 'unlock' status."));
			return true;
		}
		
		account.setState(LockState.UNLOCK);
		player.sendMessage(ChatUtil.toColour("&aYou have set your lock state to 'unlock'."));
		
		return true;
	}
	
}
