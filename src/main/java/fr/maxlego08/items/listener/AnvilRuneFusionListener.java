package fr.maxlego08.items.listener;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.runes.configurations.RuneEnchantApplicatorConfiguration;
import fr.maxlego08.items.api.runes.handlers.ItemApplicationHandler;
import fr.maxlego08.items.zcore.enums.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static fr.maxlego08.items.zcore.utils.MessageUtils.getMessage;

/**
 * Listener responsible for handling rune fusion in anvils.
 * <p>
 * This listener manages:
 * - Rune incompatibility checks (blocks fusion if incompatible runes are present)
 * - Rune merging from both items onto the result
 * - Special handling for EnchantApplicator runes (treats enchantments as bonuses)
 * <p>
 * Example: Pickaxe Effi 2 with EnchantApplicator +1 (displays Effi 3) fused with Effi 3 pickaxe
 * → Result: Effi 3 base + EnchantApplicator +1 = Effi 4 displayed
 */
public class AnvilRuneFusionListener implements Listener {

    private final ItemPlugin plugin;
    private final RuneManager runeManager;

    public AnvilRuneFusionListener(ItemPlugin plugin, RuneManager runeManager) {
        this.plugin = plugin;
        this.runeManager = runeManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getFirstItem();
        ItemStack secondItem = inventory.getSecondItem();

        // If we don't have two items, do nothing
        if (firstItem == null || secondItem == null || firstItem.getType().isAir() || secondItem.getType().isAir()) {
            return;
        }

        // Check if second item is an enchanted book
        boolean isEnchantedBook = secondItem.getType().toString().equals("ENCHANTED_BOOK");

        // Only handle items of the same type OR item + enchanted book (vanilla anvil logic)
        if (firstItem.getType() != secondItem.getType() && !isEnchantedBook) {
            return;
        }

        // Get runes from both items
        Optional<List<Rune>> firstRunesOpt = runeManager.getRunes(firstItem);
        Optional<List<Rune>> secondRunesOpt = runeManager.getRunes(secondItem);

        // If neither item has runes and it's not an enchanted book fusion, do nothing
        if (firstRunesOpt.isEmpty() && secondRunesOpt.isEmpty() && !isEnchantedBook) {
            return;
        }

        // If only the first item has runes and second is enchanted book, we need to handle this
        if (firstRunesOpt.isPresent() && isEnchantedBook) {
            // Continue to handle the fusion with runes
        } else if (firstRunesOpt.isEmpty() && secondRunesOpt.isEmpty()) {
            // No runes on either side, let vanilla handle it
            return;
        }

        List<Rune> runes1 = firstRunesOpt.orElse(new ArrayList<>());
        List<Rune> runes2 = secondRunesOpt.orElse(new ArrayList<>());

        // Check for incompatibilities between the two items
        for (Rune rune1 : runes1) {
            for (Rune rune2 : runes2) {
                if (areRunesIncompatible(rune1, rune2)) {
                    // Block the fusion
                    event.setResult(null);
                    return;
                }
            }
        }

        // Start with the FIRST item as base (preserves all runes and their formatting)
        ItemStack result = firstItem.clone();
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null) {
            return;
        }

        // Calculate base enchantments (without EnchantApplicator bonuses)
        Map<Enchantment, Integer> baseEnchants1 = calculateBaseEnchantments(firstItem, runes1);
        Map<Enchantment, Integer> baseEnchants2 = calculateBaseEnchantments(secondItem, runes2);

        // Merge base enchantments (vanilla anvil logic)
        Map<Enchantment, Integer> mergedEnchants = mergeEnchantments(baseEnchants1, baseEnchants2);

        // Remove all enchantments (including EnchantApplicator bonuses)
        for (Enchantment enchant : new HashSet<>(resultMeta.getEnchants().keySet())) {
            resultMeta.removeEnchant(enchant);
        }

        // Apply merged base enchantments
        for (Map.Entry<Enchantment, Integer> entry : mergedEnchants.entrySet()) {
            resultMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        // Re-apply EnchantApplicator bonuses from first item's runes
        for (Rune rune : runes1) {
            if (rune.getType().getActivator() instanceof ItemApplicationHandler<?> itemApplicationHandler) {
                try {
                    itemApplicationHandler.applyOnItems(plugin, resultMeta, rune.getConfiguration());
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to reapply EnchantApplicator from rune " + rune.getName() + ": " + e.getMessage());
                }
            }
        }

        // Update PDC with all runes (first item + new runes from second item)
        List<Rune> allRunes = new ArrayList<>(runes1);
        for (Rune rune2 : runes2) {
            if (!runes1.contains(rune2)) {
                allRunes.add(rune2);
            }
        }

        PersistentDataContainer pdc = resultMeta.getPersistentDataContainer();
        pdc.set(runeManager.getKey(), PersistentDataType.LIST.listTypeFrom(runeManager.getDataType()), allRunes);

        // Add lore lines for NEW runes from second item using ItemComponent (preserves colors)
        for (Rune rune2 : runes2) {
            if (!runes1.contains(rune2)) {
                String runeLine = getMessage(Message.RUNE_LINE, "%rune%", rune2.getDisplayName());
                plugin.getItemComponent().addLoreLine(resultMeta, runeLine);

                // Apply ItemApplicationHandler if this rune has one
                if (rune2.getType().getActivator() instanceof ItemApplicationHandler<?> itemApplicationHandler) {
                    try {
                        itemApplicationHandler.applyOnItems(plugin, resultMeta, rune2.getConfiguration());
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to apply ItemApplicationHandler from new rune " + rune2.getName() + ": " + e.getMessage());
                    }
                }
            }
        }

        result.setItemMeta(resultMeta);
        event.setResult(result);
    }

    /**
     * Checks if two runes are incompatible with each other.
     */
    private boolean areRunesIncompatible(Rune rune1, Rune rune2) {
        return rune1.getType().getIncompatibles().contains(rune2.getType()) ||
               rune2.getType().getIncompatibles().contains(rune1.getType());
    }

    /**
     * Calculates the base enchantments of an item without EnchantApplicator bonuses.
     * This extracts the "real" enchantments by removing the bonus levels added by runes.
     * Also handles enchanted books which store enchantments in EnchantmentStorageMeta.
     *
     * Example: Item has Effi 3 displayed, with EnchantApplicator +1 rune
     * → Base enchantment is Effi 2
     */
    private Map<Enchantment, Integer> calculateBaseEnchantments(ItemStack item, List<Rune> runes) {
        if (!item.hasItemMeta()) {
            return new HashMap<>();
        }

        ItemMeta meta = item.getItemMeta();
        Map<Enchantment, Integer> enchants;

        // Check if this is an enchanted book (uses EnchantmentStorageMeta)
        if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchants = new HashMap<>(enchantmentStorageMeta.getStoredEnchants());
        } else {
            enchants = new HashMap<>(meta.getEnchants());
        }

        // Remove EnchantApplicator bonuses to get base enchantments
        for (Rune rune : runes) {
            if (rune.getType().getActivator() instanceof ItemApplicationHandler<?> &&
                rune.getConfiguration() instanceof RuneEnchantApplicatorConfiguration config) {

                for (RuneEnchantApplicatorConfiguration.EnchantmentEvolution evolution : config.getEnchantmentEvolutions()) {
                    Enchantment enchant = evolution.enchantment();
                    int evolutionValue = evolution.evolution();

                    if (enchants.containsKey(enchant)) {
                        int currentLevel = enchants.get(enchant);
                        int baseLevel = currentLevel - evolutionValue;

                        if (baseLevel <= 0) {
                            enchants.remove(enchant);
                        } else {
                            enchants.put(enchant, baseLevel);
                        }
                    }
                }
            }
        }

        return enchants;
    }

    /**
     * Merges two enchantment maps using vanilla anvil logic.
     * - Same enchant, same level → level + 1 (if not exceeding max)
     * - Same enchant, different level → higher level
     * - Different enchants → both included (if compatible)
     */
    private Map<Enchantment, Integer> mergeEnchantments(Map<Enchantment, Integer> enchants1, Map<Enchantment, Integer> enchants2) {
        Map<Enchantment, Integer> merged = new HashMap<>(enchants1);

        for (Map.Entry<Enchantment, Integer> entry : enchants2.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level2 = entry.getValue();

            if (merged.containsKey(enchant)) {
                int level1 = merged.get(enchant);

                if (level1 == level2) {
                    // Same level: upgrade by 1 if possible (vanilla anvil behavior)
                    int newLevel = Math.min(level1 + 1, enchant.getMaxLevel());
                    merged.put(enchant, newLevel);
                } else {
                    // Different levels: take the higher one
                    merged.put(enchant, Math.max(level1, level2));
                }
            } else {
                // Enchantment only on second item: add it if compatible
                boolean compatible = true;
                for (Enchantment existingEnchant : merged.keySet()) {
                    if (enchant.conflictsWith(existingEnchant)) {
                        compatible = false;
                        break;
                    }
                }

                if (compatible) {
                    merged.put(enchant, level2);
                }
            }
        }

        return merged;
    }
}