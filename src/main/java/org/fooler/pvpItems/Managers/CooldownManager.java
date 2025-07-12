package org.fooler.pvpItems.Managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fooler.pvpItems.PvpItems;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class CooldownManager {

    private final int magnetCooldownTicks;
    private final int reflectorCooldownTicks;

    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();
    private final Map<String, Integer> durations = new HashMap<>();
    private final JavaPlugin plugin;

    public CooldownManager(PvpItems plugin) {
        this.plugin = plugin;
        final ConfigurationSection itemsSection = plugin.getConfig().getConfigurationSection("items");
        this.magnetCooldownTicks = itemsSection.getInt("magnet.cooldown-time") * 20;
        this.reflectorCooldownTicks = itemsSection.getInt("reflector.cooldown-time") * 20;
    }


    public void registerCooldownType(String key, int seconds) {
        durations.put(key, seconds * 20);
        cooldowns.putIfAbsent(key, new HashMap<>());
    }

    public boolean isOnCooldown(String key, UUID uuid) {
        Map<UUID, Long> cdMap = cooldowns.computeIfAbsent(key, k -> new HashMap<>());

        Long lastUse = cdMap.get(uuid);
        if (lastUse == null) return false;

        long elapsed = System.currentTimeMillis() - lastUse;
        long duration = durations.getOrDefault(key, 0) * 50L;
        return elapsed < duration;
    }


    public void applyCooldown(String key, UUID uuid) {
        Map<UUID, Long> cdMap = cooldowns.computeIfAbsent(key, k -> new HashMap<>());
        cdMap.put(uuid, System.currentTimeMillis());

        Player playerByUuid = Bukkit.getPlayer(uuid);
        if (playerByUuid == null) return;

        if (key.equalsIgnoreCase("magnet")) {
            playerByUuid.setCooldown(Material.PRISMARINE_SHARD, magnetCooldownTicks);
        } else if (key.equalsIgnoreCase("reflector")) {
            playerByUuid.setCooldown(Material.BRICK, reflectorCooldownTicks);
        }
    }

    public int getRemainingSeconds(String key, UUID uuid) {
        Map<UUID, Long> cdMap = cooldowns.computeIfAbsent(key, k -> new HashMap<>());

        Long lastUsage = cdMap.get(uuid);
        if (lastUsage == null) return 0;

        long elapsed = System.currentTimeMillis() - lastUsage;
        long remaining = (magnetCooldownTicks * 50L) - elapsed;

        if (remaining <= 0) {
            cdMap.remove(uuid);
        }
        return (int) Math.max(0, (remaining / 1000));
    }

}
