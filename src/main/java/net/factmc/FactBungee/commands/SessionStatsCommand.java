package net.factmc.FactBungee.commands;

import net.factmc.FactCore.FactSQL;
import net.factmc.FactCore.Session;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

public class SessionStatsCommand extends Command {

    private static final int PERCENTAGE_WIDTH = 25;
    private static final String
            PROCESSING = ChatColor.YELLOW + "Processing session data...",
            NO_DATA = ChatColor.RED + "No session data found.",
            PROCESSED = ChatColor.YELLOW + "Processed %d sessions.",
            VALUE_LABEL = ChatColor.GOLD + "%%%ds:",
            PERCENTAGE_CHAR = "#",
            PERCENTAGE_BAR = ChatColor.GOLD + "[" + ChatColor.GREEN + "%-" + PERCENTAGE_WIDTH + "s" + ChatColor.GOLD + "]",
            VALUE_BOX = ChatColor.GOLD + "(" + ChatColor.GREEN + "%s" + ChatColor.GOLD + ")",
            NUMBER = "%.2f";

    private static final String
            USAGE_PREFIX = ChatColor.RED + "Usage: /%s ",
            USAGE = USAGE_PREFIX + "<servers|server|players|player>",
            SERVER_USAGE = USAGE_PREFIX + "server <name>",
            PLAYER_USAGE = USAGE_PREFIX + "player <name>",
            PLAYER_NOT_FOUND = ChatColor.RED + "Unknown player '%s'";

    public SessionStatsCommand() {
        super("sessionstats", "factbungee.sessionstats");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length > 0) {

            if (args[0].equalsIgnoreCase("servers")) {

                sendStats(sender, "Most active servers", FactSQL.getInstance().getSessions(),
                        Session::getServer, CompletableFuture::completedFuture, "proxy");
                return;

            }

            else if (args[0].equalsIgnoreCase("server")) {

                if (args.length <= 1) {
                    sender.sendMessage(new TextComponent(SERVER_USAGE.formatted(getName())));
                    return;
                }

                String server = args[1];
                sendStats(sender, "Most active players on " + server, FactSQL.getInstance().getSessions(server),
                        Session::getPlayer, SessionStatsCommand::getName);
                return;

            }

            else if (args[0].equalsIgnoreCase("players")) {

                sendStats(sender, "Most active players", FactSQL.getInstance().getSessions(),
                        Session::getPlayer, SessionStatsCommand::getName);
                return;

            }

            else if (args[0].equalsIgnoreCase("player")) {

                if (args.length <= 1) {
                    sender.sendMessage(new TextComponent(PLAYER_USAGE.formatted(getName())));
                    return;
                }

                String playerName = args[1];
                FactSQL.getInstance().getUUID(playerName).thenAccept(uuid -> {

                    if (uuid == null)
                        sender.sendMessage(new TextComponent(PLAYER_NOT_FOUND.formatted(playerName)));
                    else
                        sendStats(sender, playerName + "'s most active servers", FactSQL.getInstance().getSessions(uuid),
                                Session::getServer, CompletableFuture::completedFuture, "proxy");

                });
                return;

            }

        }

        sender.sendMessage(new TextComponent(USAGE.formatted(getName())));

    }

    public static CompletableFuture<String> getName(UUID player) {
        return FactSQL.getInstance().getName(player);
    }

    private static <T> void sendStats(CommandSender sender, String title, CompletableFuture<List<Session>> sessionsFuture,
                                      Function<Session, T> groupingFunction, Function<T, CompletableFuture<String>> namingFunction) {
        sendStats(sender, title, sessionsFuture, groupingFunction, namingFunction, null);
    }
    private static <T> void sendStats(CommandSender sender, String title, CompletableFuture<List<Session>> sessionsFuture,
                                        Function<Session, T> groupingFunction, Function<T, CompletableFuture<String>> namingFunction, T totalKey) {
        sender.sendMessage(new TextComponent(PROCESSING));
        sessionsFuture.thenAccept(sessions -> {

            if (sessions.size() == 0) {
                sender.sendMessage(new TextComponent(NO_DATA));
                return;
            }

            Map<T, Duration> durations = new HashMap<>();
            Map<T, String> names = new HashMap<>();
            Set<CompletableFuture<?>> futures = new HashSet<>();

            for (Session session : sessions) {
                T group = groupingFunction.apply(session);

                Duration duration = Duration.between(session.getStart(), session.getEnd());
                durations.merge(group, duration, Duration::plus);

                CompletableFuture<String> future = namingFunction.apply(group);
                futures.add(future.thenAccept(name -> names.put(group, name)));

            }

            Duration maxDuration = null;
            if (totalKey != null)
                maxDuration = durations.remove(totalKey);
            List<Map.Entry<T, Duration>> durationList = durations.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).toList();
            if (totalKey == null)
                maxDuration = durationList.get(0).getValue();

            double max = maxDuration.toMillis();
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRun(() -> {

                sender.sendMessage(new TextComponent(PROCESSED.formatted(sessions.size())));

                //noinspection OptionalGetWithoutIsPresent
                int maxNameLength = names.values().stream().max(Comparator.comparingInt(String::length)).get().length();

                sender.sendMessage(new TextComponent(ChatColor.GOLD + title + ":"));
                for (Map.Entry<T, Duration> entry : durationList) {
                    String name = names.get(entry.getKey());
                    Duration duration = entry.getValue();

                    double percentage = duration.toMillis() / max;
                    String string = VALUE_LABEL.formatted(maxNameLength).formatted(name) + " " + makePercentageBar(percentage) + " "
                            + makeDurationBox(duration);
                    if (totalKey != null)
                        string += " " + makePercentageBox(percentage);

                    sender.sendMessage(new TextComponent("  " + string));

                }

            });

        });
    }

    private static String makePercentageBar(double percentage) {
        return PERCENTAGE_BAR.formatted(
                PERCENTAGE_CHAR.repeat((int) (percentage * 25))
        ).replaceFirst(" ", ChatColor.WHITE + " ").replace(' ', '.');
    }

    private static String makePercentageBox(double percentage) {
        return VALUE_BOX.formatted(NUMBER.formatted(percentage * 100) + "%");
    }

    private static String makeDurationBox(Duration duration) {
        double num = duration.toMillis();
        String string;
        if (num >= 3600000) { // 1 hour
            num /= 3600000;
            string = "hour";
        } else if (num >= 60000) { // 1 minute
            num /= 60000;
            string = "minute";
        } else {
            num /= 1000;
            string = "second";
        }

        if (num != 1)
            string += "s";
        return VALUE_BOX.formatted(NUMBER.formatted(num) + " " + string);
    }

}
