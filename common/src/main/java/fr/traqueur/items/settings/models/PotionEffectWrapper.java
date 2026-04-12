package fr.traqueur.items.settings.models;

import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record PotionEffectWrapper(
        PotionEffectType type,
        int duration,

        @Options(optional = true)
        @DefaultInt(0)
        int amplifier,

        @Options(optional = true)
        @DefaultBool(false)
        boolean ambient,

        @Options(optional = true)
        @DefaultBool(true)
        boolean showParticles,

        @Options(optional = true)
        @DefaultBool(true)
        boolean showIcon
) implements Loadable {

    public PotionEffect toPotionEffect() {
        return new PotionEffect(
                type,
                duration,
                amplifier,
                ambient,
                showParticles,
                showIcon
        );
    }
}