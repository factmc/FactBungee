package net.factmc.FactBungee.listeners;

import java.util.List;
import java.util.UUID;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.bungee.events.VotifierEvent;

import net.factmc.FactCore.CoreUtils;
import net.factmc.FactCore.FactSQL;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerVote implements Listener {
	
	public static final int VOTE_POINTS = 10;
	
	@EventHandler
	public static void onVote(VotifierEvent event) {
		Vote vote = event.getVote();
			
		UUID uuid = FactSQL.getInstance().getUUID(vote.getUsername());
		if (uuid == null) return;
		
		FactSQL.getInstance().change(FactSQL.getStatsTable(), uuid, "TOTALVOTES", 1);
		
		//Player player = Bukkit.getPlayer(UUID.fromString(uuid));
		
		/*FileConfiguration data = Data.getPlayerData(oPlayer);
		data.set("last-vote.service", vote.getServiceName());
		data.set("last-vote.address", vote.getAddress());
		data.set("last-vote.timestamp", vote.getTimeStamp());
		data.set("last-vote.local-timestamp", vote.getLocalTimestamp());
		
		LocalDate date = LocalDate.now(ZoneId.systemDefault());
		int year = date.getYear();
		int month = date.getMonthValue();
		int day = date.getDayOfMonth();
		String fullDate = (month + "-" + day + "-" + year);
		data.set("last-vote.date", fullDate);*/
		
		// Give player points
		FactSQL.getInstance().changePoints(uuid, VOTE_POINTS);
		
		// Broadcast messages
		String msg = ChatColor.translateAlternateColorCodes('&', 
				"&a%displayname%&a voted on &e%site% &aand earned &e" + VOTE_POINTS + " &apoints!");
		msg = addVoteInfo(msg, vote, uuid);
		for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			p.sendMessage(new TextComponent(msg));
		}
		
	}
	
	/*public static ItemStack getItemStack(String path, Vote vote) {
		Material material = Material.valueOf(Main.getPlugin().getConfig().getString(path + ".material").toUpperCase());
		short dataValue = Short.parseShort(Main.getPlugin().getConfig().getString(path + ".data"));
		
		ItemStack itemStack = new ItemStack(material, Main.getPlugin().getConfig().getInt(path + ".amount"), dataValue);
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		itemMeta.setDisplayName(addVoteInfo(Main.getConfigString(path + ".name"), vote));
		itemMeta.setLore(addVoteInfo(Main.getConfigStringList(path + ".lore"), vote));
		if (Main.getPlugin().getConfig().getBoolean(path + ".glow")) {
			itemMeta.addEnchant(Enchantment.DURABILITY, -1, true);
			itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}*/
	
	public static String addVoteInfo(String string, Vote vote, UUID uuid) {
		
		String prefix = CoreUtils.getPrefix(uuid);
		String suffix = CoreUtils.getSuffix(uuid);
		String name = FactSQL.getInstance().getName(uuid);
		
		string = string.replaceAll("%name%", name)
				.replaceAll("%displayname%", prefix + ChatColor.RESET + name + ChatColor.RESET + suffix);
		
		string = string.replaceAll("%site%", vote.getServiceName())
				.replaceAll("%address%", vote.getAddress());
		
		return string;
	}
	
	@Deprecated
	public static List<String> addVoteInfo(List<String> strings, Vote vote, UUID uuid) {
		int i = 0;
		for (String string : strings) {
			string = addVoteInfo(string, vote, uuid);
			strings.set(i, string);
			i++;
		}
		
		return strings;
	}
	
}