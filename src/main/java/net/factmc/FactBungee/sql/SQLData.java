package net.factmc.FactBungee.sql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import net.factmc.FactBungee.Main;
import net.factmc.FactCore.FactSQL;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLData {
	
	public static void updatePlayer(ProxiedPlayer player) {
		
		UUID uuid = player.getUniqueId();
		
		if (!FactSQL.getInstance().exists(uuid)) {
			
			Timestamp firstJoin = new Timestamp((new Date()).getTime());
			
			FactSQL.getInstance().insert(FactSQL.getStatsTable(),
					new String[] {"UUID","NAME","ADDRESS","POINTS","PLAYTIME","FIRSTJOIN","TOTALVOTES","PARKOURTIME"},
					new Object[] {uuid.toString(), player.getName(), player.getAddress().getAddress().getHostAddress(), 0, 0, firstJoin, 0, 0});
			
			Main.getPlugin().getLogger().info("Successfully added " + player.getName() + " (" + uuid + ") to the database");
			
		}
		
		else {
			
			FactSQL.getInstance().set(FactSQL.getStatsTable(), uuid, "NAME", player.getName());
			FactSQL.getInstance().set(FactSQL.getStatsTable(), uuid, "ADDRESS", player.getAddress().getAddress().getHostAddress());
			
		}
		
	}
	
	public static void saveLastOnline(ProxiedPlayer player) {
		
		Timestamp now = new Timestamp((new Date()).getTime());
		FactSQL.getInstance().set(FactSQL.getStatsTable(), player.getUniqueId(), "LASTONLINE", now);
		
	}
	
}