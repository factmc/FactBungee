package net.factmc.FactBungee.listeners;

import net.factmc.FactBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class DevelopmentMode implements Listener {
	
	private static boolean enabled = false;
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	public static boolean toggleEnabled() {
		enabled = !enabled;
		if (enabled) kickPlayers();
		return enabled;
	}
	
	
	private static void kickPlayers() {
		
		for (ProxiedPlayer player : Main.getPlugin().getProxy().getPlayers()) {
			if (!player.hasPermission("factbungee.bypass.dev-mode")) {
				player.disconnect(new TextComponent(ChatColor.RED + "This server is now in development mode"
						+ "\nIf you believe this is an error you may contact an admin"
						+ "\n" + ChatColor.BOLD + "Please try to connect again later"));
			}
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProxyPing(ProxyPingEvent event) {
		if (!enabled) return;
		
		if (event.getResponse() == null) return;
		ServerPing ping = event.getResponse();
		ping.setDescriptionComponent(new TextComponent(ChatColor.RED + "This server is currently in development mode"
				+ "\nIf you believe this is an error contact an admin"));
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PostLoginEvent event) {
		if (!enabled) return;
		
		if (!event.getPlayer().hasPermission("factbungee.bypass.dev-mode")) {
			event.getPlayer().disconnect(new TextComponent(ChatColor.RED + "This server is currently in development mode"
					+ "\nIf you believe this is an error you may contact an admin"
					+ "\n" + ChatColor.BOLD + "Please try to connect again later"));
		}
	}
	
}