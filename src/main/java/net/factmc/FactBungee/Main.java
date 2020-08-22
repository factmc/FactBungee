package net.factmc.FactBungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.factmc.FactBungee.commands.BroadcastCommand;
import net.factmc.FactBungee.commands.CmdLogToggleCommand;
import net.factmc.FactBungee.commands.DevModeCommand;
import net.factmc.FactBungee.commands.FactDataCommand;
import net.factmc.FactBungee.commands.GlobalWhitelistCommand;
import net.factmc.FactBungee.commands.SuffixCommand;
import net.factmc.FactBungee.listeners.CommandLogger;
import net.factmc.FactBungee.listeners.DevelopmentMode;
import net.factmc.FactBungee.listeners.LoginEvents;
import net.factmc.FactBungee.listeners.PlayerVote;
import net.factmc.FactBungee.listeners.ServerFallbackListener;
import net.factmc.FactBungee.listeners.WhitelistManager;
import net.factmc.FactBungee.sql.PlayTimeCounter;

public class Main extends Plugin {

	private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

	public static Plugin plugin;
	private static File configFile;
	private static Configuration config;

	@Override
	public void onEnable() {
		plugin = this;

		loadConfig();
		WhitelistManager.init();

		registerEvents();
		registerCommands();
	}

	@Override
	public void onDisable() {
		for (PlayTimeCounter c : PlayTimeCounter.times) {

			c.saveTime();

		}
		PlayTimeCounter.times.clear();

		plugin = null;
	}

	private void loadConfig() {

		configFile = new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		if (!configFile.exists()) {
			try (InputStream stream = getResourceAsStream("config.yml")) {

				configFile.getParentFile().mkdir();
				Files.copy(stream, Paths.get(configFile.getPath()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			config = PROVIDER.load(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Configuration getConfig() {
		return config;
	}

	public static void saveConfig() {

		try {
			PROVIDER.save(config, configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void registerEvents() {
		List<Listener> listeners = new ArrayList<Listener>();
		listeners.add(new CommandLogger());
		listeners.add(new LoginEvents());
		listeners.add(new PlayTimeCounter(null));
		PlayTimeCounter.startAutoRefresh();
		listeners.add(new PlayerVote());
		listeners.add(new WhitelistManager());
		listeners.add(new DevelopmentMode());
		listeners.add(new ServerFallbackListener());

		for (Listener listener : listeners) {
			getProxy().getPluginManager().registerListener(plugin, listener);
		}
	}

	public void registerCommands() {
		getProxy().getPluginManager().registerCommand(plugin, new SuffixCommand());
		getProxy().getPluginManager().registerCommand(plugin, new FactDataCommand());
		getProxy().getPluginManager().registerCommand(plugin, new GlobalWhitelistCommand());
		getProxy().getPluginManager().registerCommand(plugin, new DevModeCommand());
		getProxy().getPluginManager().registerCommand(plugin, new BroadcastCommand());
		getProxy().getPluginManager().registerCommand(plugin, new CmdLogToggleCommand());
		// plugin.getProxy().getPluginManager().registerCommand(plugin, new
		// ReloadCommand());
	}

	public static Plugin getPlugin() {
		return plugin;
	}

}