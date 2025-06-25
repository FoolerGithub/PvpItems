package org.fooler.pvpItems.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fooler.pvpItems.PvpItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class MagnetCooldown {
    private final Map<UUID, Long> playersOnCooldown = new HashMap<>();
    private final int magnetCooldownTicks;
    private final JavaPlugin plugin;

    public MagnetCooldown(PvpItems plugin) {
        this.plugin = plugin;
        this.magnetCooldownTicks = plugin.getConfig().getInt("cooldown-time", 30) * 20;
    }

    public boolean isOnCooldown(UUID uuid) {
        Long lastUse = playersOnCooldown.get(uuid);
        if (lastUse == null) return false;

        long elapsed = System.currentTimeMillis() - lastUse;
        return elapsed < (magnetCooldownTicks * 50L);
    }

    public void applyCooldown(UUID uuid) {
        playersOnCooldown.put(uuid, System.currentTimeMillis());
        Player playerByUuid = Bukkit.getPlayer(uuid);
        if (playerByUuid == null) return;
        playerByUuid.setCooldown(Material.PRISMARINE_SHARD, magnetCooldownTicks);
    }

    public Map<UUID, Long> getCooldownsMap() {
        return this.playersOnCooldown;
    }

    public int getRemainingSeconds(UUID uuid) {
        Long lastUsage = playersOnCooldown.get(uuid);
        if (lastUsage == null) return 0;

        long elapsed = System.currentTimeMillis() - lastUsage;
        long remaining = (magnetCooldownTicks * 50L) - elapsed;

        return (int) Math.max(0, (remaining / 1000));
    }

}
