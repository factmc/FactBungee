package net.factmc.FactBungee.commands;

import net.factmc.FactBungee.listeners.WhitelistManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class GlobalWhitelistCommand extends Command {

	public GlobalWhitelistCommand() {
		super("gwhitelist", "gwhitelist.use", "gwl");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("gwhitelist.use"))
			return;

		if (args.length > 0) {

			if (args[0].equalsIgnoreCase("enable")) {

				if (args.length < 2) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /gwhitelist enable <true|false>"));
					return;
				}

				boolean enabled = Boolean.parseBoolean(args[1]);
				WhitelistManager.setEnabled(enabled);
				sender.sendMessage(new TextComponent(ChatColor.GREEN + "Whitelist enabled: " + enabled));
				return;

			}

			else if (args[0].equalsIgnoreCase("add")) {

				if (args.length < 2) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /gwhitelist add <player>"));
					return;
				}

				WhitelistManager.getUUID(args[1]).thenAccept((uuid) -> {

					if (uuid == null) {
						sender.sendMessage(new TextComponent(ChatColor.RED + args[1] + " does not exist"));
					}

					else if (WhitelistManager.addPlayer(uuid)) {
						sender.sendMessage(
								new TextComponent(ChatColor.GREEN + "Added " + args[1] + " to the whitelist"));
					}

					else {
						sender.sendMessage(new TextComponent(ChatColor.RED + args[1] + " is already whitelisted"));
					}

				});
				return;

			}

			else if (args[0].equalsIgnoreCase("remove")) {

				if (args.length < 2) {
					sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /gwhitelist remove <player>"));
					return;
				}

				WhitelistManager.getUUID(args[1]).thenAccept((uuid) -> {

					if (uuid == null) {
						sender.sendMessage(new TextComponent(ChatColor.RED + args[1] + " does not exist"));
					}

					else if (WhitelistManager.removePlayer(uuid)) {
						sender.sendMessage(
								new TextComponent(ChatColor.GREEN + "Removed " + args[1] + " from the whitelist"));
					}

					else {
						sender.sendMessage(new TextComponent(ChatColor.RED + args[1] + " is not whitelisted"));
					}

				});
				return;
			}

		}

		sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /gwhitelist <enable|add|remove>"));

	}

}
