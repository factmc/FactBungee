package net.factmc.FactBungee.listeners;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;

import net.factmc.FactBungee.Main;
import net.factmc.FactCore.FactSQL;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WhitelistManager implements Listener {

	private static boolean enabled = false;
	private static List<UUID> whitelistedPlayers = new ArrayList<UUID>();

	public static void init() {

		enabled = Main.getConfig().getBoolean("whitelist.enabled");
		for (String uuid : Main.getConfig().getStringList("whitelist.players")) {
			whitelistedPlayers.add(UUID.fromString(uuid));
		}

	}

	public static void setEnabled(boolean enabled) {
		WhitelistManager.enabled = enabled;
		updateConfig();
	}

	public static boolean addPlayer(UUID uuid) {
		if (whitelistedPlayers.contains(uuid))
			return false;
		whitelistedPlayers.add(uuid);
		updateConfig();
		return true;
	}

	public static boolean removePlayer(UUID uuid) {
		if (!whitelistedPlayers.contains(uuid))
			return false;
		whitelistedPlayers.remove(uuid);
		updateConfig();
		return true;
	}

	private static void updateConfig() {
		Main.getConfig().set("whitelist.enabled", enabled);

		List<String> players = new ArrayList<String>();
		for (UUID uuid : whitelistedPlayers) {
			players.add(uuid.toString());
		}
		Main.getConfig().set("whitelist.players", players);

		Main.saveConfig();
	}

	public static CompletableFuture<UUID> getUUID(String name) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
		if (player != null)
			return CompletableFuture.completedFuture(player.getUniqueId());

		return FactSQL.getInstance().getUUID(name).thenApply((uuid) -> {

			if (uuid != null)
				return uuid;
			else {

				try {

					HttpURLConnection connection = (HttpURLConnection) new URL(
							"https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
					if (connection.getResponseCode() == 200) {

						@SuppressWarnings("unchecked")
						Map<String, Object> map = new Gson()
								.fromJson(new InputStreamReader(connection.getInputStream()), Map.class);
						String id = ((String) map.get("id")).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
								"$1-$2-$3-$4-$5");
						return UUID.fromString(id);

					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			return null;

		});
	}

	@EventHandler
	public void onPlayerLogin(LoginEvent event) {
		if (enabled && !whitelistedPlayers.contains(event.getConnection().getUniqueId())) {
			event.setCancelReason(new TextComponent("You are not whitelisted on this server"));
			event.setCancelled(true);
		}
	}

}
