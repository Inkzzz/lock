package io.inkstudios.spigot.lock.util;

import net.md_5.bungee.api.ChatColor;

public class ChatUtil {
	
	private ChatUtil() {
		throw new IllegalStateException("Cannot create an instance of " + this.getClass().getSimpleName());
	}
	
	public static String toColour(String context) {
		return ChatColor.translateAlternateColorCodes('&', context);
	}
	
}
