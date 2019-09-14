package net.factmc.FactBungee.sql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import net.factmc.FactBungee.Main;
import net.factmc.FactCore.FactSQLConnector;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLData {
	
	public static void updatePlayer(ProxiedPlayer player) {
		
		UUID uuid = player.getUniqueId();
		boolean exists = FactSQLConnector.getCount(FactSQLConnector.getStatsTable(), new String[] {"UUID"},
				new Object[] {uuid.toString()}) > 0;
		
		if (!exists) {
			
			Timestamp firstJoin = new Timestamp((new Date()).getTime());
			
			FactSQLConnector.insertRow(FactSQLConnector.getStatsTable(),
					new String[] {"UUID","NAME","ADDRESS","POINTS","PLAYTIME","FIRSTJOIN","TOTALVOTES","PARKOURTIME"},
					new Object[] {uuid.toString(), player.getName(), player.getAddress().getAddress().getHostAddress(), 0, 0, firstJoin, 0, 0});
			
			Main.getPlugin().getLogger().info("Successfully added " + player.getName() + " (" + uuid + ") to the database");
			
		}
		
		else {
			
			FactSQLConnector.setValue(FactSQLConnector.getStatsTable(), uuid, "NAME", player.getName());
			FactSQLConnector.setValue(FactSQLConnector.getStatsTable(), uuid, "ADDRESS", player.getAddress().getAddress().getHostAddress());
			
		}
		
	}
	
	public static void saveLastOnline(ProxiedPlayer player) {
		
		Timestamp now = new Timestamp((new Date()).getTime());
		FactSQLConnector.setValue(FactSQLConnector.getStatsTable(), player.getUniqueId(), "LASTONLINE", now);
		
	}
	
}