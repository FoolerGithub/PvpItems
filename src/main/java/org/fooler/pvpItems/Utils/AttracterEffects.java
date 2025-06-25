package org.fooler.pvpItems.Utils;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum AttracterEffects {
    WEAKNESS(PotionEffectType.WEAKNESS, 1, 60),
    SLOWNESS(PotionEffectType.SLOW, 3, 60),
    BLIND(PotionEffectType.BLINDNESS, 1, 40);

    private final PotionEffectType effectType;
    private final int amplifier;
    private final int duration;


    AttracterEffects(PotionEffectType effectType, int amplifier, int duration) {
        this.effectType = effectType;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public PotionEffectType getPotionEffect() {
        return effectType;
    }
    public int getAmplifier() {
        return amplifier;
    }
    public int getDuration() {
        return duration;
    }
    public PotionEffect addPotionEffect() {
        return new PotionEffect(effectType, duration, amplifier);
    }
}
