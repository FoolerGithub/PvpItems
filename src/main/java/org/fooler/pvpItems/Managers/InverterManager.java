package org.fooler.pvpItems.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.fooler.pvpItems.PvpItems;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InverterManager {
    private final PvpItems plugin = PvpItems.getInstance();
    private final Map<UUID, Long> active = new ConcurrentHashMap<>();
    private static final long DURATION_MS = 5 * 60 * 1000;

    public void activate(Player p) {
        active.put(p.getUniqueId(), System.currentTimeMillis());
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> deactivate(p), DURATION_MS / 50);
    }

    public boolean isActive(UUID id) {
        return active.containsKey(id)
            && System.currentTimeMillis() - active.get(id) < DURATION_MS;
    }

    public void deactivate(Player p) {
        active.remove(p.getUniqueId());
    }

}
