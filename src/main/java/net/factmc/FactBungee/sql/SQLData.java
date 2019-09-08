package net.factmc.FactBungee.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.sql.Types;

import net.factmc.FactBungee.Main;
import net.factmc.FactCore.FactSQLConnector;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLData {
	
	public static void updatePlayer(ProxiedPlayer player) {
		
		UUID uuid = player.getUniqueId();
		boolean exists = false;
		try {
			PreparedStatement statement = FactSQLConnector.getMysql().getConnection()
					.prepareStatement("SELECT * FROM " + FactSQLConnector.getStatsTable() + " WHERE `UUID`=?");
			statement.setString(1, uuid.toString());
			
			ResultSet results = statement.executeQuery();
			if (results.next()) exists = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		if (!exists) {
			
			Timestamp firstJoin = new Timestamp((new Date()).getTime());
			
			try {
				
				PreparedStatement insert = FactSQLConnector.getMysql().getConnection()
						.prepareStatement("INSERT INTO " + FactSQLConnector.getStatsTable()
						+ " (UUID,NAME,ADDRESS,DISCORD,POINTS,PLAYTIME,FIRSTJOIN,TOTALVOTES,PARKOURTIME) VALUE (?,?,?,?,?,?,?,?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, player.getName());
				insert.setString(3, player.getAddress().getAddress().getHostAddress());
				insert.setNull(4, Types.BIGINT);
				insert.setInt(5, 0);
				insert.setLong(6, 0);
				insert.setTimestamp(7, firstJoin);
				insert.setInt(8, 0);
				insert.setInt(9, 0);
				insert.executeUpdate();
				
				Main.getPlugin().getLogger().info("Successfully added " + player.getName() + " (" + uuid + ") to the database");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
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