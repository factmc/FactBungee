package net.factmc.FactBungee.sql;

import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import net.factmc.FactBungee.Main;
import net.factmc.FactCore.FactSQL;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLData {
	
	public static void updatePlayer(ProxiedPlayer player) {
		
		UUID uuid = player.getUniqueId();
		
		FactSQL.getInstance().exists(uuid).thenAccept((exists) -> {
			if (!exists) {
				
				Timestamp firstJoin = new Timestamp((new Date()).getTime());
				
				FactSQL.getInstance().insert(FactSQL.getStatsTable(),
						new String[] {"UUID", "NAME", "ADDRESS", "IPUSED", "POINTS", "PLAYTIME", "FIRSTJOIN", "TOTALVOTES", "PARKOURTIME"},
						new Object[] {
								uuid.toString(), player.getName(),
								((InetSocketAddress) player.getSocketAddress()).getHostString(),
								player.getPendingConnection().getVirtualHost().getHostString(),
								0, 0, firstJoin, 0, 0
						});
				
				Main.getPlugin().getLogger().info("Successfully added " + player.getName() + " (" + uuid + ") to the database");
				
			}
			else {
				
				FactSQL.getInstance().update(FactSQL.getStatsTable(), new String[] {"NAME", "ADDRESS", "IPUSED"},
						new Object[] {
								player.getName(),
								((InetSocketAddress) player.getSocketAddress()).getHostString(),
								player.getPendingConnection().getVirtualHost().getHostString()
						},"`UUID`=?", uuid.toString());
				
			}
		});
		
	}
	
	public static void saveLastOnline(ProxiedPlayer player) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		FactSQL.getInstance().set(FactSQL.getStatsTable(), player.getUniqueId(), "LASTONLINE", now);
		
	}
	
}