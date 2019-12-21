package net.factmc.FactBungee.commands;

import net.factmc.FactCore.CoreUtils;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.SuffixNode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SuffixCommand extends Command implements TabExecutor {

	public SuffixCommand() {
		super("suffix");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission("factbungee.suffix")) {
			sender.sendMessage(new TextComponent(ChatColor.YELLOW + "You must be "
					+ ChatColor.AQUA + ChatColor.BOLD + "MVP"
					+ ChatColor.YELLOW + " to do that!"));
			return;
		}
		
		
		else if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Only players can do that!"));
			return;
		}
		ProxiedPlayer player = (ProxiedPlayer) sender;
		
		if (args.length == 0) {
			
			setSuffix(player.getUniqueId(), null);
			
			sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Your suffix has been removed"));
			return;
		}
		
		else {
			
			UUID uuid = player.getUniqueId();
			String suffix = CoreUtils.combine(args, 0);
			/*if (suffix.startsWith("-p") && args.length > 1) {
				
				User user = BungeePerms.getInstance().getPermissionsManager().getUser(args[1]);
				uuid = user.getUUID();
				if (args.length < 3) {
					setSuffix(uuid, suffix);
					sender.sendMessage(new TextComponent(ChatColor.YELLOW + user.getName() + "'s suffix has been removed"));
					return;
				}
				
			}*/
			
			if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', suffix)).length() > 16) {
				
				sender.sendMessage(new TextComponent(ChatColor.RED + "Your suffix must be 16 characters or less"));
				return;
				
			}
			
			suffix = ChatColor.RESET + "[" + suffix + ChatColor.RESET + "]";
			setSuffix(uuid, suffix);
			
			suffix = ChatColor.translateAlternateColorCodes('&', suffix);
			sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Your suffix has been changed to: " + suffix));
			return;
			
		}
		
	}
	
	public void setSuffix(UUID uuid, String suffix) {
		
		User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
		Set<Node> suffixes = user.getNodes().stream().filter(NodeType.SUFFIX::matches).collect(Collectors.toSet());
		suffixes.forEach(node -> user.data().remove(node));
		
		Node node = SuffixNode.builder(suffix, 0).build();
		user.data().add(node);
		LuckPermsProvider.get().getUserManager().saveUser(user);
		 
	}
	
	

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
	
}