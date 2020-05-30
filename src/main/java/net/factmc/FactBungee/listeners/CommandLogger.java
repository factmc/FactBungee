package net.factmc.FactBungee.listeners;

import java.util.HashMap;
import java.util.Map;
import net.factmc.FactCore.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
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
			CoreUtils.getPrefix(sender.getUniqueId()).thenAccept((prefix) -> {
				
				String cmd = event.getMessage().replaceAll(" ", " " + ChatColor.AQUA);
				
				/*String msg = "&7[&4&lAdmin Only&7]&r %prefix%&c%player% &6used command: &c%cmd%";
				msg = msg.replaceAll("%prefix%", prefix);
				msg = ChatColor.translateAlternateColorCodes('&', msg);
				msg = msg.replaceAll("%player%", sender.getName());
				msg = msg.replaceAll("%cmd%", cmd);*/
				
				String msg = PREFIX + ChatColor.translateAlternateColorCodes('&', prefix) + sender.getName()
						+ " " + ChatColor.BLUE + "used command: " + ChatColor.AQUA + cmd;

				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					
					if (player != sender && enabled(player) && player.getServer().getInfo().getName().equals(sender.getServer().getInfo().getName())
							&& player.hasPermission("factbungee.seecmds")) {
						
						if (player.hasPermission("factbungee.seecmds.all")) {
							player.sendMessage(new TextComponent(msg));
						}
						
						else {
							CoreUtils.isAbove(player.getUniqueId(), sender.getUniqueId()).thenAccept((isAbove) -> {
								if (isAbove) player.sendMessage(new TextComponent(msg));
							});
						}
						
					}
					
				}
				
			});
			
		}
		
	}
	
}