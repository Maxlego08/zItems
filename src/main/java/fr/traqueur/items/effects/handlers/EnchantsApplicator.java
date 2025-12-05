package fr.traqueur.items.effects.handlers;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.settings.EnchantsSettings;
import org.bukkit.inventory.meta.ItemMeta;

@AutoEffect(value = "ENCHANTS_APPLICATOR")
public class EnchantsApplicator implements EffectHandler.NoEventEffectHandler<EnchantsSettings> {

    @Override
    public void handle(EffectContext context, EnchantsSettings settings) {
        boolean error = false;
        for (EnchantsSettings.EnchantSetting enchantment : settings.enchantments()) {
            int evolution = enchantment.computeEvolutionValue();
            int level = context.itemSource().getItemMeta().getEnchants().getOrDefault(enchantment.wrapper().enchantment(), 0);
            if (level == 0) {
                error = true;
                break;
            }
            if (evolution < 0 && level + evolution < 0) {
                error = true;
                break;
            }
        }

        if (error) {
            return;
        }

        ItemMeta meta = context.itemSource().getItemMeta();
        if (meta != null) {
            for (EnchantsSettings.EnchantSetting enchantment : settings.enchantments()) {
                int evolution = enchantment.computeEvolutionValue();
                int level = meta.getEnchants().getOrDefault(enchantment.wrapper().enchantment(), 0);

                if (level + evolution == 0) {
                    meta.removeEnchant(enchantment.wrapper().enchantment());
                } else {
                    meta.addEnchant(enchantment.wrapper().enchantment(), level + evolution, true);
                }
            }
            context.itemSource().setItemMeta(meta);
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
