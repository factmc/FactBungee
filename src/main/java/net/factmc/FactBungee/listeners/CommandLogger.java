package net.factmc.FactBungee.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.lucko.luckperms.LuckPerms;
import net.factmc.FactBungee.Main;
import net.factmc.FactCore.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class CommandLogger implements Listener {
	
	public static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + ChatColor.BOLD + "Command Log" +
				ChatColor.GRAY + "]" + ChatColor.RESET + " ";
	private static final Map<ProxiedPlayer, Boolean> TOGGLES = new HashMap<ProxiedPlayer, Boolean>();
	
	public static boolean toggle(ProxiedPlayer player) {
		boolean boo = !enabled(player);
		TOGGLES.put(player, boo);
		return boo;
	}
	
	public static boolean enabled(ProxiedPlayer player) {
		if (!TOGGLES.containsKey(player)) return true;
		else return TOGGLES.get(player);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCommand(ChatEvent event) {
		
		if (event.isCancelled()) return;
		
		if (event.isCommand()) {
			
			ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
			String prefix = CoreUtils.getPrefix(sender.getUniqueId());
			String cmd = event.getMessage().replaceAll(" ", " " + ChatColor.AQUA);
			
			/*String msg = "&7[&4&lAdmin Only&7]&r %prefix%&c%player% &6used command: &c%cmd%";
			msg = msg.replaceAll("%prefix%", prefix);
			msg = ChatColor.translateAlternateColorCodes('&', msg);
			msg = msg.replaceAll("%player%", sender.getName());
			msg = msg.replaceAll("%cmd%", cmd);*/
			
			String msg = PREFIX + ChatColor.translateAlternateColorCodes('&', prefix) + sender.getName()
					+ " " + ChatColor.BLUE + "used command: " + ChatColor.AQUA + cmd;

			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				
				if (player.hasPermission("factbungee.seecmds") && enabled(player)) {
					
					if (isAbove(player, sender) || player.hasPermission("factbungee.seecmds.all")) {
					
						if (player.getServer().getInfo().getName().equals(sender.getServer().getInfo().getName())) {
							
							if (player != sender) {
						
								player.sendMessage(new TextComponent(msg));
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public static boolean isAbove(ProxiedPlayer player, ProxiedPlayer otherPlayer) {
		
		int rank = LuckPerms.getApi().getGroup(LuckPerms.getApi().getUser(player.getUniqueId()).getPrimaryGroup()).getWeight().getAsInt();
		int otherRank = LuckPerms.getApi().getGroup(LuckPerms.getApi().getUser(otherPlayer.getUniqueId()).getPrimaryGroup()).getWeight().getAsInt();
		
		return rank >= otherRank;
		
	}
	
	
	
	private static final TextComponent IP = new TextComponent(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "" +
			ChatColor.UNDERLINE + "play.factmc.net");
	private static final TextComponent IP_MESSAGE = new TextComponent(ChatColor.DARK_AQUA + "Please use " + IP.getText() +
			ChatColor.DARK_AQUA + " to connect");
	private static final Title IP_TITLE = ProxyServer.getInstance().createTitle().reset().title(IP).subTitle(IP_MESSAGE)
			.fadeIn(10).stay(60).fadeOut(20);
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent event) {
		
		ProxiedPlayer player = event.getPlayer();
		String usedIP = player.getPendingConnection().getVirtualHost().getHostString();
		if (!usedIP.endsWith("play.factmc.net")) {
			
			ProxyServer.getInstance().getScheduler().schedule(Main.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					player.sendMessage(IP_MESSAGE);
					player.sendTitle(IP_TITLE);
				}
			}, 3L, TimeUnit.SECONDS);
			
		}
		
	}
	
}