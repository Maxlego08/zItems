package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneEnchantApplicatorConfiguration;
import fr.maxlego08.items.api.runes.handlers.ItemApplicationHandler;
import fr.maxlego08.items.exceptions.ItemEnchantException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantApplicator implements ItemApplicationHandler<RuneEnchantApplicatorConfiguration>, RuneActivator {

    @Override
    public void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, RuneEnchantApplicatorConfiguration runeConfiguration) throws ItemEnchantException {
        boolean error = false;
        for (RuneEnchantApplicatorConfiguration.EnchantmentEvolution enchantmentEvolution : runeConfiguration.getEnchantmentEvolutions()) {
            Enchantment enchantment = enchantmentEvolution.enchantment();
            int evolution = enchantmentEvolution.evolution();
            int level = itemMeta.getEnchants().getOrDefault(enchantment, 0);
            if (level == 0) {
                error = true;
                break;
            }

            if(evolution < 0 && level + evolution < 0) {
                error = true;
                break;
            }
        }

        if (error) {
            throw new ItemEnchantException("Enchantment not found");
        } else {
            for (RuneEnchantApplicatorConfiguration.EnchantmentEvolution enchantmentEvolution : runeConfiguration.getEnchantmentEvolutions()) {
                Enchantment enchantment = enchantmentEvolution.enchantment();
                int evolution = enchantmentEvolution.evolution();
                int level = itemMeta.getEnchants().getOrDefault(enchantment, 0);

                if (level + evolution == 0) {
                    itemMeta.removeEnchant(enchantment);
                } else {
                    itemMeta.addEnchant(enchantment, level + evolution, true);
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
