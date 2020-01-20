package net.factmc.FactBungee.commands;

import net.factmc.FactBungee.listeners.CommandLogger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CmdLogToggleCommand extends Command {

	public CmdLogToggleCommand() {
		super("togglecommandlog", "factbungee.seecmds", "togglecmdlog", "tcl", "toggleseecmds");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!(sender instanceof ProxiedPlayer)) return;
		
		String toggle = "now see";
		if (!CommandLogger.toggle((ProxiedPlayer) sender)) {
			toggle = "no longer see";
		}
		
		sender.sendMessage(new TextComponent(CommandLogger.PREFIX + ChatColor.BLUE + "You can " + toggle + " the command log"));
		
	}

}