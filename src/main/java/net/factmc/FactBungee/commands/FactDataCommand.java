package net.factmc.FactBungee.commands;

import net.factmc.FactBungee.Main;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FactDataCommand extends Command implements TabExecutor {
	
	private static final String PREFIX = ChatColor.GOLD + "[" + ChatColor.DARK_GREEN +
			ChatColor.BOLD + "FactData" + ChatColor.GOLD + "] ";
	private Map<CommandSender, UUID> resetConfirmMap = new HashMap<CommandSender, UUID>();

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
						
						// Get Points
						if (args[1].equalsIgnoreCase("get")) {
							
							if (args.length < 3) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points get <player>"));
								return;
							}
							
							FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"NAME", "POINTS"}, "`NAME`=?", args[2]).thenAccept((list) -> {
								
								if (list.isEmpty()) {
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[2]));
									return;
								}
								
								int points = (int) list.get(0).get("POINTS");
								String name = (String) list.get(0).get("NAME");
								sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " has " + points + " points"));
								
							});
							return;
							
						}
						
						// Set Points
						else if (args[1].equalsIgnoreCase("set")) {
							
							if (args.length < 4) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points set <player> <amount>"));
								return;
							}
							
							FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"UUID", "NAME"}, "`NAME`=?", args[2]).thenAccept((list) -> {
								
								if (list.isEmpty()) {
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[2]));
									return;
								}
								
								UUID uuid = UUID.fromString((String) list.get(0).get("UUID"));
								String name = (String) list.get(0).get("NAME");
								try {
									
									int newPoints = Integer.parseInt(args[3]);
									if (newPoints < 0) throw new NumberFormatException();
									FactSQL.getInstance().setPoints(uuid, newPoints);
									
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " now has " + newPoints + " points"));
									
								} catch (NumberFormatException e) {
									sender.sendMessage(new TextComponent(ChatColor.RED + "That is not a valid number"));
								}
								
							});
							return;
							
						}
						
						// Add Points
						else if (args[1].equalsIgnoreCase("add")) {
							
							if (args.length < 4) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points add <player> <amount>"));
								return;
							}
							
							FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"UUID", "NAME", "POINTS"}, "`NAME`=?", args[2]).thenAccept((list) -> {
								
								if (list.isEmpty()) {
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[2]));
									return;
								}
								
								UUID uuid = UUID.fromString((String) list.get(0).get("UUID"));
								int points = (int) list.get(0).get("POINTS");
								String name = (String) list.get(0).get("NAME");
								try {
									
									int add = Integer.parseInt(args[3]);
									if (add < 0) throw new NumberFormatException();
									int newPoints = points + add;
									FactSQL.getInstance().setPoints(uuid, newPoints);
									
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " now has " + newPoints + " points"));
									
								} catch (NumberFormatException e) {
									sender.sendMessage(new TextComponent(ChatColor.RED + "That is not a valid number"));
								}
								
							});
							return;
							
						}
						
						// Remove Points
						else if (args[1].equalsIgnoreCase("remove")) {
							
							if (args.length < 4) {
								sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
										+ " points remove <player> <amount>"));
								return;
							}
							
							FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"UUID", "NAME", "POINTS"}, "`NAME`=?", args[2]).thenAccept((list) -> {
								
								if (list.isEmpty()) {
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[2]));
									return;
								}
								
								UUID uuid = UUID.fromString((String) list.get(0).get("UUID"));
								int points = (int) list.get(0).get("POINTS");
								String name = (String) list.get(0).get("NAME");
								try {
									
									int remove = Integer.parseInt(args[3]);
									if (remove < 0) throw new NumberFormatException();
									int newPoints = points - remove;
									FactSQL.getInstance().setPoints(uuid, newPoints);
									
									sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + " now has " + newPoints + " points"));
									
								} catch (NumberFormatException e) {
									sender.sendMessage(new TextComponent(ChatColor.RED + "That is not a valid number"));
								}
								
							});
							return;
							
						}
						
					}
					
					// Points Command Help
					sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName()
							+ " points <get|set|add|remove> <player> [amount]"));
					return;
				
				}
			}
			
			else if (args[0].equalsIgnoreCase("listplayers")) {
				if (sender.hasPermission("factbungee.factdata.listplayers")) {
					
					FactSQL.getInstance().select(FactSQL.getStatsTable(), "NAME", "").thenAccept((list) -> {
						
						if (list.isEmpty()) {
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.RED + "No known players found."
									+ " This is likely a bug, please contact a senior staff member immediately"));
							return;
						}
						
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + "Known players (" + list.size() + "):"));
						for (Object name : list) {
							sender.sendMessage(new TextComponent(ChatColor.GREEN + " - " + (String) name));
						}
						
					});
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("ip")) {
				if (sender.hasPermission("factbungee.factdata.ip")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " ip <player|ip>"));
						return;
					}
					
					CompletableFuture<String> future;
					if (args[1].matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
						future = CompletableFuture.completedFuture(args[1]);
					}
					else {
						future = FactSQL.getInstance().select(FactSQL.getStatsTable(), "ADDRESS", "`NAME`=?", args[1]).thenApply((list) -> {
							if (list.isEmpty()) return null;
							else return (String) list.get(0);
						});
					}
					
					future.thenAccept((ip) -> {
						
						if (ip == null) {
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "No valid address found for " + args[1]));
							return;
						}
						
						FactSQL.getInstance().select(FactSQL.getStatsTable(), "NAME", "`ADDRESS`=?", ip).thenAccept((list) -> {
							
							if (list.isEmpty()) {
								sender.sendMessage(new TextComponent(PREFIX + ChatColor.RED + "No players found for " + ip));
								return;
							}
							
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + "Players found for " + ip + ":"));
							for (Object name : list) {
								sender.sendMessage(new TextComponent(ChatColor.GREEN + " - " + (String) name));
							}
							
						});
						
					});
					return;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("seen")) {
				if (sender.hasPermission("factbungee.factdata.seen")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " seen <player>"));
						return;
					}
					
					FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"UUID", "NAME", "LASTONLINE"}, "`NAME`=?", args[1]).thenAccept((list) -> {
						
						if (list.isEmpty()) {
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
							return;
						}
						
						String message;
						ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString((String) list.get(0).get("UUID")));
						if (player != null)
							message = " is currently online on " + player.getServer().getInfo().getName();
						else {
							LocalDateTime lastOnline = ((Timestamp) list.get(0).get("LASTONLINE")).toLocalDateTime();
							message = " was last online at " + convertLocalDateTime(lastOnline);
						}
						String name = (String) list.get(0).get("NAME");
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + message.replaceAll(" ", " " + ChatColor.GREEN)));
						
					});
					return;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("playtime")) {
				if (sender.hasPermission("factbungee.factdata.playtime")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " playtime <player>"));
						return;
					}
					
					FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"NAME", "PLAYTIME"}, "`NAME`=?", args[1]).thenAccept((list) -> {
						
						if (list.isEmpty()) {
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
							return;
						}
						
						long seconds = (long) list.get(0).get("PLAYTIME");
						String message = " has a playtime of " + CoreUtils.convertSeconds(seconds);
						String name = (String) list.get(0).get("NAME");
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + name + message.replaceAll(" ", " " + ChatColor.GREEN)));
						
					});
					return;
					
				}
			}
			
			else if (args[0].equalsIgnoreCase("reset")) {
				if (sender.hasPermission("factbungee.factdata.reset")) {
					
					if (args.length < 2) {
						sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.getName() + " reset <player>"));
						return;
					}
					
					
					if (resetConfirmMap.containsKey(sender) && args[1].equalsIgnoreCase("confirm")) {
						
						UUID uuid = resetConfirmMap.get(sender);
						resetConfirmMap.remove(sender);
						FactSQL.getInstance().getName(uuid).thenAccept((name) -> {
							
							FactSQL.getInstance().delete(FactSQL.getAccessTable(), "`UUID`=?", uuid.toString());
							FactSQL.getInstance().delete(FactSQL.getAchievementsTable(), "`UUID`=?", uuid.toString());
							FactSQL.getInstance().delete(FactSQL.getModerationTable(), "`USER`=?", uuid.toString());
							FactSQL.getInstance().delete(FactSQL.getFriendsTable(), "`UUID`=? OR `FRIEND`=?", new Object[] {uuid.toString(), uuid.toString()});
							FactSQL.getInstance().delete(FactSQL.getOptionsTable(), "`UUID`=?", uuid.toString());
							FactSQL.getInstance().delete(FactSQL.getStatsTable(), "`UUID`=?", uuid.toString());
							
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.GREEN + "Successfully reset " + name));
							
						});
						return;
					}
					
					
					FactSQL.getInstance().select(FactSQL.getStatsTable(), new String[] {"UUID", "NAME"}, "`NAME`=?", args[1]).thenAccept((list) -> {
						
						if (list.isEmpty()) {
							sender.sendMessage(new TextComponent(PREFIX + ChatColor.YELLOW + "Unable to find " + args[1]));
							return;
						}
						
						String name = (String) list.get(0).get("NAME");
						sender.sendMessage(new TextComponent(PREFIX + ChatColor.RED + "" + ChatColor.BOLD + "CAUTION!"
								+ ChatColor.YELLOW + " This action is irreversible. To confirm " + ChatColor.YELLOW + "deleting "
								+ ChatColor.BOLD + "ALL" + ChatColor.YELLOW + " data on " + name + " type "
								+ ChatColor.GOLD + "/factdata reset confirm " + ChatColor.YELLOW + "within " + ChatColor.YELLOW + "30 seconds"));
						
						resetConfirmMap.put(sender, UUID.fromString((String) list.get(0).get("UUID")));
						Main.getPlugin().getProxy().getScheduler().schedule(Main.getPlugin(), new Runnable() {
							@Override
							public void run() {
								resetConfirmMap.remove(sender);
							}
						}, 30, TimeUnit.SECONDS);
						
					});
					return;
					
				}
			}
			
		}
		
		String list = "";
		if (sender.hasPermission("factbungee.factdata.points")) list += list.isEmpty() ? "points" : "|points";
		if (sender.hasPermission("factbungee.factdata.listplayers")) list += list.isEmpty() ? "listplayers" : "|listplayers";
		if (sender.hasPermission("factbungee.factdata.seen")) list += list.isEmpty() ? "seen" : "|seen";
		if (sender.hasPermission("factbungee.factdata.playtime")) list += list.isEmpty() ? "playtime" : "|playtime";
		if (sender.hasPermission("factbungee.factdata.ip")) list += list.isEmpty() ? "ip" : "|ip";
		if (sender.hasPermission("factbungee.factdata.reset")) list += list.isEmpty() ? "reset" : "|reset";
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
			
			else if (args[0].equalsIgnoreCase("listplayers")) {
				return toList();
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
		if (sender.hasPermission("factbungee.factdata.listplayers")) list.add("listplayers");
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