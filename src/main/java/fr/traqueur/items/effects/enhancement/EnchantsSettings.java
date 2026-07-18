package fr.traqueur.items.effects.enhancement;

import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.items.api.settings.models.EnchantmentWrapper;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;

import java.util.List;

public record EnchantsSettings(
        List<EnchantSetting> enchantments,
        @Options(optional = true) List<Material> applicableMaterials,
        @Options(optional = true) List<Tag<Material>> applicableTags,
        @Options(optional = true) @DefaultBool(false) boolean applicabilityBlacklisted
) implements EffectSettings {

    public enum Evolution {
        INCREASE,
        DECREASE
    }

    public record EnchantSetting(@Options(inline = true) EnchantmentWrapper wrapper, Evolution evolution) implements Loadable {

        public int computeEvolutionValue() {
            return switch (evolution) {
                case INCREASE -> wrapper.level();
                case DECREASE -> -wrapper.level();
            };
        }

    }

}
