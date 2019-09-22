package net.factmc.FactBungee.commands;

import net.factmc.FactCore.CoreUtils;
import net.factmc.FactCore.FactSQL;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class FactDataCommand extends Command implements TabExecutor {
	
	private static final String PREFIX = ChatColor.GOLD + "[" + ChatColor.DARK_GREEN +
			ChatColor.BOLD + "FactData" + ChatColor.GOLD + "] ";

	public FactDataCommand() {
		super("factdata");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission("factbungee.factdata")) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to use that command"));
			return;
		}
		
		
		if (args.length > 0) {
			
			if (args[0].equalsIgnoreCase("points")) {
				if (sender.hasPermission("factbungee.factdata.points")) {
				
					if (args.length > 1) {
						
						// Get Player UUID
						UUID uuid = null;
						if (args.length > 2) {
							uuid = FactSQL.getInstance().getUUID(args[2]);
							if (uuid == null) {
								sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[2]));
								return;
							}
						}
						
						
						// Get Points
						if (args[1].equalsIgnoreCase("get")) {
							
							if (args.length < 3) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points get <player>"));
								return;
							}
							
							int points = FactSQL.getInstance().getPoints(uuid);
							String name = FactSQL.getInstance().getName(uuid);
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " has " + points + " points"));
							return;
							
						}
						
						// Set Points
						else if (args[1].equalsIgnoreCase("set")) {
							
							if (args.length < 4) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points set <player> <amount>"));
								return;
							}
							
							try {
								
								int newPoints = Integer.parseInt(args[3]);
								if (newPoints < 0) throw new NumberFormatException();
								FactSQL.getInstance().setPoints(uuid, newPoints);
								String name = FactSQL.getInstance().getName(uuid);
								
								sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " now has " + newPoints + " points"));
								
							} catch (NumberFormatException e) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "That is not a valid number"));
							}
							return;
							
						}
						
						// Add Points
						else if (args[1].equalsIgnoreCase("add")) {
							
							if (args.length < 4) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points add <player> <amount>"));
								return;
							}
							
							try {
								
								int add = Integer.parseInt(args[3]);
								if (add < 0) throw new NumberFormatException();
								FactSQL.getInstance().changePoints(uuid, add);
								String name = FactSQL.getInstance().getName(uuid);
								
								int points = FactSQL.getInstance().getPoints(uuid);
								sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " now has " + points + " points"));
								
							} catch (NumberFormatException e) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "That is not a valid number"));
							}
							return;
							
						}
						
						// Remove Points
						else if (args[1].equalsIgnoreCase("remove")) {
							
							if (args.length < 4) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points remove <player> <amount>"));
								return;
							}
							
							try {
								
								int remove = Integer.parseInt(args[3]);
								if (remove < 0) throw new NumberFormatException();
								FactSQL.getInstance().changePoints(uuid, -remove);
								String name = FactSQL.getInstance().getName(uuid);
								
								int points = FactSQL.getInstance().getPoints(uuid);
								sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " now has " + points + " points"));
								
							} catch (NumberFormatException e) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "That is not a valid number"));
							}
							return;
							
						}
						
					}
					
					// Points Command Help
					sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
							+ " points <get|set|add|remove> <player> [amount]"));
					return;
				
				}
			}
			
			else if (args[0].equalsIgnoreCase("ip")) {
				if (sender.hasPermission("factbungee.factdata.ip")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " ip <player|ip>"));
						return;
					}
					
					String ip = null;
					if (args[1].matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$"))
						ip = args[1];
					else {
						UUID uuid = FactSQL.getInstance().getUUID(args[1]);
						if (uuid == null) {
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
							return;
						}
						ip = FactSQL.getInstance().get(FactSQL.getStatsTable(), uuid, "ADDRESS").toString();
					}
					
					if (ip == null) {
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "No valid address found for " + args[1]));
						return;
					}
					
					
					List<Object> names = FactSQL.getInstance().select(FactSQL.getStatsTable(), "NAME", "`ADDRESS`=?", ip);
					if (names.isEmpty()) {
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.RED + "No players found for " + ip));
						return;
					}
					
					sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + "Players found for " + ip + ":"));
					for (Object name : names) {
						sender.sendMessage(new TextComponent(ChatColor.GREEN + " - " + name.toString()));
					}
					return;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("seen")) {
				if (sender.hasPermission("factbungee.factdata.seen")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " seen <player>"));
						return;
					}
					
					UUID uuid = FactSQL.getInstance().getUUID(args[1]);
					if (uuid == null) {
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
						return;
					}
					
					String message;
					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
					if (player != null)
						message = " is currently online on " + player.getServer().getInfo().getName();
					else {
						LocalDateTime lastOnline = ((Timestamp) FactSQL.getInstance().get(FactSQL.getStatsTable(), uuid, "LASTONLINE")).toLocalDateTime();
						message = " was last online at " + convertLocalDateTime(lastOnline);
					}
					String name = FactSQL.getInstance().getName(uuid);
					sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + message.replaceAll(" ", " " + ChatColor.GREEN)));
					return;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("playtime")) {
				if (sender.hasPermission("factbungee.factdata.playtime")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " playtime <player>"));
						return;
					}
					
					UUID uuid = FactSQL.getInstance().getUUID(args[1]);
					if (uuid == null) {
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
						return;
					}
					
					long seconds = (long) FactSQL.getInstance().get(FactSQL.getStatsTable(), uuid, "PLAYTIME");
					String message = " has a playtime of " + CoreUtils.convertSeconds(seconds);
					String name = FactSQL.getInstance().getName(uuid);
					sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + message.replaceAll(" ", " " + ChatColor.GREEN)));
					return;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("reset")) {
				if (sender.hasPermission("factbungee.factdata.reset")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " reset <player>"));
						return;
					}
					
					UUID uuid = FactSQL.getInstance().getUUID(args[1]);
					if (uuid == null) {
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
						return;
					}
					
					String name = FactSQL.getInstance().getName(uuid);
					FactSQL.getInstance().delete(FactSQL.getStatsTable(), "`UUID`=?", uuid.toString());
					sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + "Successfully reset " + name));
					return;
					
				}
			}
			
		}
		
		String list = "";
		if (sender.hasPermission("factbungee.factdata.points")) list += list.equals("") ? "points" : "|points";
		if (sender.hasPermission("factbungee.factdata.seen")) list += list.equals("") ? "seen" : "|seen";
		if (sender.hasPermission("factbungee.factdata.playtime")) list += list.equals("") ? "playtime" : "|playtime";
		if (sender.hasPermission("factbungee.factdata.ip")) list += list.equals("") ? "ip" : "|ip";
		if (sender.hasPermission("factbungee.factdata.reset")) list += list.equals("") ? "reset" : "|reset";
		sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " <" + list + ">"));
		
	}
	
	
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission("factbungee.factdata")) {
			return toList();
		}
		
		
		if (args.length > 0) {
			
			if (args[0].equalsIgnoreCase("points")) {
				if (sender.hasPermission("factbungee.factdata.points")) {
					
					if (args.length > 2) {
						
						if (args.length < 4) {
							return filter(toList(ProxyServer.getInstance().getPlayers()), args[2]);
						}
						
						return toList();
						
					}
					
					// Points Command Help
					List<String> list = toList("get", "set", "add", "remove");
					if (args.length > 1) return filter(list, args[1]);
					else return list;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("ip")
					|| args[0].equalsIgnoreCase("seen") || args[0].equalsIgnoreCase("playtime")) {
				if (sender.hasPermission("factbungee.factdata." + args[0].toLowerCase())) {
					
					if (args.length < 3 && args.length > 1) {
						return filter(toList(ProxyServer.getInstance().getPlayers()), args[1]);
					}
					
					return toList();
					
				}
			}
			
		}
		
		List<String> list = toList();
		if (sender.hasPermission("factbungee.factdata.points")) list.add("points");
		if (sender.hasPermission("factbungee.factdata.seen")) list.add("seen");
		if (sender.hasPermission("factbungee.factdata.playtime")) list.add("playtime");
		if (sender.hasPermission("factbungee.factdata.ip")) list.add("ip");
		if (sender.hasPermission("factbungee.factdata.reset")) list.add("reset");
		if (args.length > 0) return filter(list, args[0]);
		else return list;
		
	}
	
	public static List<String> toList(String... strings) {
		
		List<String> list = new ArrayList<String>();
		for (String string : strings) {
			list.add(string);
		}
		return list;
		
	}
	public static List<String> toList(Collection<ProxiedPlayer> collection) {
		
		List<String> list = new ArrayList<String>();
		for (ProxiedPlayer player : collection) {
			list.add(player.getName());
		}
		return list;
		
	}
	public static List<String> filter(List<String> list, String start) {
		if (start.equals("")) return list;
		List<String> filtered = new ArrayList<String>();
		for (String string : list) {
			if (string.toLowerCase().startsWith(start.toLowerCase())) {
				filtered.add(string);
			}
		}
		return filtered;
	}
	
	
	
	private static String convertLocalDateTime(LocalDateTime time) {
		
		String hour = String.valueOf(time.getHour());
		if (time.getHour() < 10) hour = "0" + time.getHour();
		String minute = String.valueOf(time.getMinute());
		if (time.getMinute() < 10) minute = "0" + time.getMinute();
		String second = String.valueOf(time.getSecond());
		if (time.getSecond() < 10) second = "0" + time.getSecond();
		String date = time.getMonthValue() + "/" + time.getDayOfMonth() + "/" + time.getYear();
		
		return hour + ":" + minute + ":" + second + " on " + date;
		
	}
	
}