package net.factmc.FactBungee;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.factmc.FactBungee.commands.BroadcastCommand;
import net.factmc.FactBungee.commands.CmdLogToggleCommand;
import net.factmc.FactBungee.commands.DevModeCommand;
import net.factmc.FactBungee.commands.FactDataCommand;
import net.factmc.FactBungee.commands.SuffixCommand;
import net.factmc.FactBungee.listeners.CommandLogger;
import net.factmc.FactBungee.listeners.DevelopmentMode;
import net.factmc.FactBungee.listeners.LoginEvents;
import net.factmc.FactBungee.listeners.PlayerVote;
import net.factmc.FactBungee.sql.PlayTimeCounter;

public class Main extends Plugin {
	
	public static Plugin plugin;
	
    @Override
    public void onEnable() {
    	plugin = this;
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
    
    
    
    public void registerEvents() {
    	List<Listener> listeners = new ArrayList<Listener>();
    	listeners.add(new CommandLogger());
    	listeners.add(new LoginEvents());
    	listeners.add(new PlayTimeCounter(null));
    	PlayTimeCounter.startAutoRefresh();
    	listeners.add(new PlayerVote());
    	listeners.add(new DevelopmentMode());
    	
        for (Listener listener : listeners) {
        	getProxy().getPluginManager().registerListener(plugin, listener);
        }
    }
    
    public void registerCommands() {
    	getProxy().getPluginManager().registerCommand(plugin, new SuffixCommand());
    	getProxy().getPluginManager().registerCommand(plugin, new FactDataCommand());
    	getProxy().getPluginManager().registerCommand(plugin, new DevModeCommand());
    	getProxy().getPluginManager().registerCommand(plugin, new BroadcastCommand());
    	getProxy().getPluginManager().registerCommand(plugin, new CmdLogToggleCommand());
    	//plugin.getProxy().getPluginManager().registerCommand(plugin, new ReloadCommand());
    }
    
    
    
    public static Plugin getPlugin() {
        return plugin;
    }
    
}