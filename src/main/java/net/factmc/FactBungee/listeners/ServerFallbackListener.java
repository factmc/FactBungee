package net.factmc.FactBungee.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerFallbackListener implements Listener {

	@EventHandler
	public void onServerKicked(ServerKickEvent event) {
		if (!event.getKickedFrom().getName().equals("hub")) {

			List<BaseComponent> messages = new ArrayList<BaseComponent>();
			messages.add(new TextComponent(ChatColor.RED + "You have been kicked from "
					+ event.getKickedFrom().getName() + ": " + ChatColor.RESET));
			messages.addAll(Arrays.asList(event.getKickReasonComponent()));

			event.getPlayer().sendMessage(messages.toArray(new BaseComponent[messages.size()]));
			event.setCancelServer(ProxyServer.getInstance().getServerInfo("hub"));
			event.setCancelled(true);

		}
	}

}