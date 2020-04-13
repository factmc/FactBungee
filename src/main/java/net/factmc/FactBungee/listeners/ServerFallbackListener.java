package net.factmc.FactBungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerFallbackListener implements Listener {
	
	@EventHandler
	public void onServerKicked(ServerKickEvent event) {
		if (!event.getKickedFrom().getName().equals("hub")) {
			event.setCancelServer(ProxyServer.getInstance().getServerInfo("hub"));
			event.setCancelled(true);
		}
	}
	
}