package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneEnchantApplicatorConfiguration;
import fr.maxlego08.items.exceptions.ItemEnchantException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantApplicator implements RuneActivator<RuneEnchantApplicatorConfiguration> {

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneEnchantApplicatorConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        return Set.of();
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, RuneEnchantApplicatorConfiguration runeConfiguration) {}

    @Override
    public ItemStack applyOnItems(ItemPlugin plugin, ItemStack itemStack, RuneEnchantApplicatorConfiguration runeConfiguration) throws ItemEnchantException {
        boolean error = false;
        for (RuneEnchantApplicatorConfiguration.EnchantmentEvolution enchantmentEvolution : runeConfiguration.getEnchantmentEvolutions()) {
            Enchantment enchantment = enchantmentEvolution.enchantment();
            int evolution = enchantmentEvolution.evolution();
            int level = itemStack.getEnchantments().getOrDefault(enchantment, 0);
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
            ItemMeta meta = itemStack.getItemMeta();
            for (RuneEnchantApplicatorConfiguration.EnchantmentEvolution enchantmentEvolution : runeConfiguration.getEnchantmentEvolutions()) {
                Enchantment enchantment = enchantmentEvolution.enchantment();
                int evolution = enchantmentEvolution.evolution();
                int level = itemStack.getEnchantments().getOrDefault(enchantment, 0);

                if (level + evolution == 0) {
                    meta.removeEnchant(enchantment);
                } else {
                    meta.addEnchant(enchantment, level + evolution, true);
                }
            }
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
