package io.inkstudios.spigot.lock.command;

import io.inkstudios.spigot.lock.util.ChatUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

/**
 * Handles the /locks command
 */
public class LocksCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Stream.of(
				"&7&l-------------------------",
				"&b&l/lock &7- &fEnables lock mode.",
				"&b&l/unlock &7- &fEnables unlock mode.",
				" ",
				"&7&l* &fOnce the command is executed, you can interact a block to either lock or unlock it.",
				"&7&l* &fTo disable the lock or unlock mode, reenter the command.",
				"&7&l-------------------------"
		).map(ChatUtil::toColour).forEach(sender::sendMessage);
		return false;
	}
	
}
