package org.fooler.pvpItems.Listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.fooler.pvpItems.PvpItems;
import org.fooler.pvpItems.Utils.AttracterEffects;
import org.fooler.pvpItems.Managers.CooldownManager;

import java.util.List;
import java.util.UUID;

public class MagnetCastListener implements Listener  {
    private static final Double RADIUS = 7.0;
    private final CooldownManager cooldownManager;
    private final PvpItems plugin;
    private final FileConfiguration config;

    public MagnetCastListener(PvpItems plugin, CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
        this.config = plugin.getConfig();
        this.plugin = plugin;
    }

    @EventHandler
    public void itemUsage(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack hand = e.getItem();

        if (this.rg(player)) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (hand == null || !isMagnet(hand)) return;

        if (cooldownManager.isOnCooldown("magnet", uuid)) {
            int seconds = cooldownManager.getRemainingSeconds("magnet", uuid);

            String msg = config.getString("items.magnet.cooldown-message", "Магнит на перезарядке: %time% секунд.")
                    .replace("%time%", String.valueOf(seconds));
            player.sendMessage(msg);
            e.setCancelled(true);
            return;
        }

        hand.setAmount(hand.getAmount() - 1);

        cooldownManager.applyCooldown("magnet", uuid);

        String pullSound = config.getString("items.magnet.push-sound", "ENTITY_PLAYER_LEVELUP");

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Location center = player.getLocation();

                    for (Entity entity : player.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
                        if (!(entity instanceof Player livingPlayer)) continue;
                        if (entity == player) continue;

                        Vector dir = center.toVector().subtract(entity.getLocation().toVector()).normalize();

                        for (AttracterEffects entryEffect : AttracterEffects.values()) {
                            livingPlayer.addPotionEffect(entryEffect.addPotionEffect());
                        }
                        livingPlayer.setVelocity(dir.multiply(1.55));

                    }

                    Sound sound;
                    try {
                        sound = Sound.valueOf(pullSound.toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        sound = Sound.ENTITY_PLAYER_LEVELUP;
                    }
                    center.getWorld().playSound(center, sound, 1.0F, 1.0F);

                }, 2L);

    }

    public boolean isMagnet(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        return meta.getPersistentDataContainer().has(
                new NamespacedKey(plugin, "magnet"), PersistentDataType.BYTE
        );
    }

    private boolean rg(Player player) {
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            return false;
        } else {
            Location loc = player.getLocation();
            com.sk89q.worldedit.util.Location wg = BukkitAdapter.adapt(loc);
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            ApplicableRegionSet regions = query.getApplicableRegions(wg);
            List<String> disable = this.config.getStringList("items.magnet.disabled-regions");
            String priority = null;
            int max = Integer.MIN_VALUE;

            for(ProtectedRegion region : regions) {
                if (region.getPriority() > max) {
                    max = region.getPriority();
                    priority = region.getId();
                }
            }

            return priority != null && disable.contains(priority);
        }
    }
}



