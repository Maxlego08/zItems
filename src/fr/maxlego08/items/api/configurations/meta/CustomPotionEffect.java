package fr.maxlego08.items.api.configurations.meta;

import org.bukkit.potion.PotionEffectType;

public record CustomPotionEffect(
    boolean overwrite,
    PotionEffectType type,
    int duration,
    int amplifier,
    boolean ambient,
    boolean particles,
    boolean icon
) {}
