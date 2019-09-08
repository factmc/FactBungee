package net.factmc.FactBungee.commands;

import net.factmc.FactBungee.listeners.DevelopmentMode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class DevModeCommand extends Command {

	public DevModeCommand() {
		super("devmode");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission("factbungee.devmode")) return;
		
		if (args.length > 0 && args[0].equalsIgnoreCase("toggle")) {
			
			boolean enabled = DevelopmentMode.toggleEnabled();
			sender.sendMessage(new TextComponent(ChatColor.BLUE + "Development Mode is now " + getStatus(enabled)));
			return;
			
		}
		
		sender.sendMessage(new TextComponent(ChatColor.BLUE + "Development Mode is currently "
				+ getStatus(DevelopmentMode.isEnabled())));
		return;
		
	}
	
	public static String getStatus(boolean enabled) {
		if (DevelopmentMode.isEnabled()) return ChatColor.GREEN + "" + ChatColor.BOLD + "enabled";
		else return ChatColor.RED + "" + ChatColor.BOLD + "disabled";
	}
	
}