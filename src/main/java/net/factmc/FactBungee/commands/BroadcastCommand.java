package net.factmc.FactBungee.commands;

import net.factmc.FactCore.CoreUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class BroadcastCommand extends Command {
	
	public BroadcastCommand() {
		super("bbroadcast", "factbungee.broadcast");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Please specify a message"));
			return;
		}
		
		String message = CoreUtils.combine(args, 0);
		message = ChatColor.translateAlternateColorCodes('&', message);
		
		ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.GOLD + "[" + ChatColor.DARK_RED + "BROADCAST" + ChatColor.GOLD + "] " +
				ChatColor.GREEN + message));
		
	}

}
