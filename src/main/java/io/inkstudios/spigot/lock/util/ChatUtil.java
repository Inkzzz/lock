package io.inkstudios.spigot.lock.util;

import net.md_5.bungee.api.ChatColor;

public class ChatUtil {
	
	private ChatUtil() {
		throw new IllegalStateException("Cannot create an instance of " + this.getClass().getSimpleName());
	}
	
	/**
	 * Colorizes the specified context {@code context}
	 *
	 * <p>Colorizes the specified context with use of <li>'&'</li></p>
	 *
	 * @param context string to colorize
	 * @return colorized string
	 */
	public static String toColour(String context) {
		return ChatColor.translateAlternateColorCodes('&', context);
	}
	
}
