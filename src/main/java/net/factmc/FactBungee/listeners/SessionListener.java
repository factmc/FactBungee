package net.factmc.FactBungee.listeners;

import net.factmc.FactCore.FactSQL;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;

public class SessionListener implements Listener {

    private HashMap<UUID, Long> LAST_SWITCH_TIMES = new HashMap<>(), JOIN_TIMES = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        JOIN_TIMES.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerSwitchServer(ServerSwitchEvent event) {
        if (event.getFrom() != null)
            updateSessions(event.getPlayer(), event.getFrom(), false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        if (event.getPlayer().getServer() != null)
            updateSessions(event.getPlayer(), event.getPlayer().getServer().getInfo(), true);
    }

    private void updateSessions(ProxiedPlayer player, ServerInfo leftServer, boolean remove) {
        UUID uuid = player.getUniqueId();
        String server = leftServer.getName();

        long current = System.currentTimeMillis();
        Long lastSwitch = remove ? LAST_SWITCH_TIMES.remove(uuid) : LAST_SWITCH_TIMES.put(uuid, current);
        long join = remove ? JOIN_TIMES.remove(uuid) : JOIN_TIMES.get(uuid);

        if (lastSwitch == null) {

            FactSQL.getInstance().saveSession(uuid, server, join, current);
            FactSQL.getInstance().saveSession(uuid, "proxy", join, current);

        } else {

            FactSQL.getInstance().saveSession(uuid, server, lastSwitch, current);
            FactSQL.getInstance().updateSession(uuid, "proxy", current);

        }
    }

}
