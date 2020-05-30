package net.factmc.FactBungee.listeners;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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
		
		FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"UUID", "TOTALVOTES", "POINTS"}, "`NAME`=?", vote.getUsername()).thenAccept((list) -> {
			
			if (!list.isEmpty()) {
				UUID uuid = UUID.fromString((String) list.get(0).get("UUID"));
				int totalVotes = (int) list.get(0).get("TOTALVOTES");
				int points = (int) list.get(0).get("POINTS");
				
				FactSQL.getInstance().update(FactSQL.getStatsTable(), new String[] {"TOTALVOTES", "POINTS"}, new Object[] {totalVotes, points}, "`UUID`=?", uuid);
				
				String msgFormat = ChatColor.translateAlternateColorCodes('&', 
						"&a%displayname%&a voted on &e%site% &aand earned &e" + VOTE_POINTS + " &apoints!");
				addVoteInfo(msgFormat, vote, uuid).thenAccept((msg) -> {
					TextComponent component = new TextComponent(msg);
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						p.sendMessage(component);
					}
				});
			}
			
		});
		
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
	
	public static String addVoteInfo(String string, Vote vote, String prefix, String name, String suffix) {
		string = string.replaceAll("%name%", name).replaceAll("%displayname%", prefix + ChatColor.RESET + name + ChatColor.RESET + suffix);
		string = string.replaceAll("%site%", vote.getServiceName()).replaceAll("%address%", vote.getAddress());
		return string;
	}
	
	public static CompletableFuture<String> addVoteInfo(String string, Vote vote, UUID uuid) {
		CompletableFuture<String> nameFuture = FactSQL.getInstance().getName(uuid);
		CompletableFuture<String[]> prefixSuffixFuture = CoreUtils.getPrefixSuffix(uuid);
		return CompletableFuture.allOf(nameFuture, prefixSuffixFuture).thenApply((v) -> {
			
			String prefix = prefixSuffixFuture.join()[0];
			String suffix = prefixSuffixFuture.join()[1];
			String name = nameFuture.join();
			
			return addVoteInfo(string, vote, prefix, name, suffix);
			
		});
	}
	
	public static CompletableFuture<List<String>> addVoteInfo(List<String> strings, Vote vote, UUID uuid) {
		CompletableFuture<String> nameFuture = FactSQL.getInstance().getName(uuid);
		CompletableFuture<String[]> prefixSuffixFuture = CoreUtils.getPrefixSuffix(uuid);
		return CompletableFuture.allOf(nameFuture, prefixSuffixFuture).thenApply((v) -> {
			
			String prefix = prefixSuffixFuture.join()[0];
			String suffix = prefixSuffixFuture.join()[1];
			String name = nameFuture.join();
			
			for (int i = 0; i < strings.size(); i++) {
				strings.set(i, addVoteInfo(strings.get(i), vote, prefix, name, suffix));
			}
			return strings;
			
		});
	}
	
}