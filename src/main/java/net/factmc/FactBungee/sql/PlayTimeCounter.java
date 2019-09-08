package net.factmc.FactBungee.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.factmc.FactBungee.Main;
import net.factmc.FactCore.FactSQLConnector;

public class PlayTimeCounter implements Listener {
	
	public static final int POINTS_PER_HALF_HOUR = 5;
	
	public static List<PlayTimeCounter> times = new ArrayList<PlayTimeCounter>();
	
	protected ProxiedPlayer player;
	protected Date start;
	protected Date end;
	boolean online;
	
	public PlayTimeCounter(ProxiedPlayer player) {
		
		this.player = player;
		this.start = new Date();
		this.end = null;
		this.online = true;
		if (player == null) this.online = false;
		
		times.add(this);
		
	}
	
	public ProxiedPlayer getPlayer() {
		return this.player;
	}
	
	public void saveTime() {
		
		//times.remove(this);
		
		if (this.online) {
			
			this.online = false;
			this.end = new Date();
			
			long miliDiff = this.end.getTime() - this.start.getTime();
			long add = miliDiff / 1000;
			
			long current = FactSQLConnector.getLongValue(FactSQLConnector.getStatsTable(), this.getPlayer().getUniqueId(), "PLAYTIME");
			
			FactSQLConnector.setValue(FactSQLConnector.getStatsTable(), this.getPlayer().getUniqueId(), "PLAYTIME", current + add);
			
		}
		
	}
	
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PostLoginEvent event) {
		
		ProxiedPlayer player = event.getPlayer();
		SQLData.updatePlayer(player);
		times.add(new PlayTimeCounter(player));
		
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerDisconnectEvent event) {
		
		SQLData.saveLastOnline(event.getPlayer());
		
		PlayTimeCounter remove = null;
		ProxiedPlayer player = event.getPlayer();
		List<PlayTimeCounter> t = new ArrayList<PlayTimeCounter>();
		t.addAll(times);
		for (PlayTimeCounter p : t) {
			if (p.online) {
			
				if (p.getPlayer().equals(player)) {
					
					p.saveTime();
					remove = p;
					
				}
			
			}
		}
		
		if (remove != null) times.remove(remove);
		
	}
	
	
	public static void startAutoRefresh() {
		Main.getPlugin().getProxy().getScheduler().schedule(Main.getPlugin(), new Runnable() {

			@Override
			public void run() {
				refresh();
				
				for (ProxiedPlayer player : Main.getPlugin().getProxy().getPlayers()) {
					player.sendMessage(new TextComponent(ChatColor.YELLOW + "" + ChatColor.ITALIC
							+ "You have received " + POINTS_PER_HALF_HOUR + " points for playing!"));
					FactSQLConnector.changePoints(player.getUniqueId(), POINTS_PER_HALF_HOUR);
				}
			}
			
		}, 30, 30, TimeUnit.MINUTES);
	}
	
	public static void refresh() {
		
		List<PlayTimeCounter> t = new ArrayList<PlayTimeCounter>();
		t.addAll(times);
		for (PlayTimeCounter p : t) {
			if (p.online) {
				p.saveTime();
			}
		}
		times.clear();
		
		for (ProxiedPlayer player : Main.getPlugin().getProxy().getPlayers()) {
			times.add(new PlayTimeCounter(player));
		}
		
	}
	
}