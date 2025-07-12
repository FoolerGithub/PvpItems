package org.fooler.pvpItems.Listeners;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.fooler.pvpItems.Managers.CooldownManager;
import org.fooler.pvpItems.Managers.InverterManager;
import org.fooler.pvpItems.PvpItems;

import java.util.Set;
import java.util.UUID;

public class InverterCastListener implements Listener {

    private final PvpItems plugin;
//    private final Map<UUID, Long> active = new ConcurrentHashMap<>();
    private final CooldownManager cooldownManager;
    private final FileConfiguration config;
    private final InverterManager inverterManager;

    private static final Set<PotionEffectType> NEGATIVE = Set.of(
            PotionEffectType.BLINDNESS, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.HUNGER,
            PotionEffectType.WITHER, PotionEffectType.POISON, PotionEffectType.BAD_OMEN, PotionEffectType.SATURATION, PotionEffectType.WEAKNESS
    );


    public InverterCastListener(PvpItems plugin, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.cooldownManager = cooldownManager;
        this.config = plugin.getConfig();
        this.inverterManager = plugin.getInverterManager();
    }


    @EventHandler
    public void onReflectorUse(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Action action = e.getAction();

        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = e.getPlayer();
        if (item == null || !isReflector(item)) return;
        UUID uuid = player.getUniqueId();

        if (cooldownManager.isOnCooldown("reflector", uuid)) {
            int seconds = cooldownManager.getRemainingSeconds("reflector", uuid);

            String msg = config.getString("items.reflector.cooldown-message", "Рефлектор на перезарядке: %time% секунд.")
                    .replace("%time%", String.valueOf(seconds));
            player.sendMessage(msg);
            e.setCancelled(true);
            return;
        }

        item.setAmount(item.getAmount() - 1);

        cooldownManager.applyCooldown("reflector", uuid);
        inverterManager.activate(player);

        Sound sound;
        String pullSound = config.getString("items.reflector.push-sound", "ENTITY_ENDER_DRAGON_HURT");

        try {
            sound = Sound.valueOf(pullSound.toUpperCase());
        } catch (IllegalArgumentException ex) {
            sound = Sound.ENTITY_PLAYER_LEVELUP;
        }
        Location loc = player.getLocation();
        loc.getWorld().playSound(loc, sound, 1.0F, 1.0F);
    }

    @EventHandler
    public void onPotionExplosion(PotionSplashEvent e) {

        ThrownPotion potion     = e.getPotion();
        ProjectileSource source = potion.getShooter();
        if (!(source instanceof Player thrower)) return;

        boolean hasBad = potion.getEffects().stream()
                .map(PotionEffect::getType)
                .anyMatch(NEGATIVE::contains);

        if (!hasBad) return;

        for (LivingEntity entity : e.getAffectedEntities()) {
            if (!(entity instanceof Player target)) continue;
            if (!inverterManager.isActive(target.getUniqueId())) continue;
            if (target.getUniqueId().equals(thrower.getUniqueId())) continue;

            for (PotionEffect pe : potion.getEffects()) {
                if (!NEGATIVE.contains(pe.getType())) continue;

                int duration = (int) (pe.getDuration() * e.getIntensity(target));
                PotionEffect reflected = new PotionEffect(pe.getType(), duration, pe.getAmplifier());

                thrower.addPotionEffect(reflected);

                e.setIntensity(target,0.0);
            }
        }
    }

    public boolean isReflector(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;

        return meta.getPersistentDataContainer().has(
                new NamespacedKey(plugin, "reflector"), PersistentDataType.BYTE
        );
    }
}
