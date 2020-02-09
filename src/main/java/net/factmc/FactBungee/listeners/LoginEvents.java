package net.factmc.FactBungee.listeners;
import java.util.concurrent.TimeUnit;

import net.factmc.FactBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginEvents implements Listener {
	
	private static final String IP = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" +
			ChatColor.UNDERLINE + "play.factmc.net";
	private static final String IP_MESSAGE = ChatColor.DARK_AQUA + "Please use " + IP +
			ChatColor.DARK_AQUA + " to connect";
	private static final Title IP_TITLE = createTitle(IP, IP_MESSAGE);
	
	private static Title title = null;
	private static String message = null;
	
	public void setLoginTitle(String title, String subtitle) {
		LoginEvents.title = createTitle(ChatColor.translateAlternateColorCodes('&', title), ChatColor.translateAlternateColorCodes('&', subtitle));
	}
	public void removeLoginTitle() {
		LoginEvents.title = null;
	}
	
	public void setLoginMessage(String message) {
		if (message == null) LoginEvents.message = null;
		LoginEvents.message = ChatColor.translateAlternateColorCodes('&', message);
	}
	
	
	private static Title createTitle(String title, String subtitle) {
		
		return ProxyServer.getInstance().createTitle().reset().title(new TextComponent(title)).subTitle(new TextComponent(subtitle))
				.fadeIn(10).stay(100).fadeOut(20);
		
	}
	
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent event) {
		
		ProxiedPlayer player = event.getPlayer();
		final Title title;
		final String message;
		
		String usedIP = player.getPendingConnection().getVirtualHost().getHostString();
		if (!(usedIP.equals("srv.factmc.net") || usedIP.equals("play.factmc.net"))) {
			title = IP_TITLE;
			message = IP_MESSAGE;
		}
		else {
			title = LoginEvents.title;
			message = LoginEvents.message;
		}
		
		if (title != null || message != null) {
			
			ProxyServer.getInstance().getScheduler().schedule(Main.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					if (message != null) player.sendMessage(new TextComponent(message));
					if (title != null) player.sendTitle(title);
				}
				
			}, 3L, TimeUnit.SECONDS);
			
		}
		
	}
	
}