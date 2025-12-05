package fr.traqueur.items.listeners;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.ItemsManager;
import fr.traqueur.items.api.settings.models.DisabledEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listener that prevents disabled enchantments from being applied to custom items.
 * Handles three scenarios:
 * <ul>
 *     <li>Enchanting table enchantments</li>
 *     <li>Enchanting table offers</li>
 *     <li>Anvil enchantments (books and items)</li>
 * </ul>
 */
public class DisableEnchantsListener implements Listener {

    /**
     * Checks if an item has any enchantment that matches a disabled enchantment.
     *
     * @param itemStack the item to check
     * @param disabledEnchantment the disabled enchantment to check against
     * @return true if the item has a matching enchantment
     */
    private boolean hasDisabledEnchantment(ItemStack itemStack, DisabledEnchantment disabledEnchantment) {
        if (!itemStack.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = itemStack.getItemMeta();

        // Check enchantment storage meta (for books)
        if (meta instanceof EnchantmentStorageMeta enchantMeta) {
            return enchantMeta.getStoredEnchants().entrySet().stream()
                    .anyMatch(entry -> disabledEnchantment.matches(entry.getKey(), entry.getValue()));
        }

        // Check regular enchantments
        return itemStack.getEnchantments().entrySet().stream()
                .anyMatch(entry -> disabledEnchantment.matches(entry.getKey(), entry.getValue()));
    }

    /**
     * Handles anvil events to prevent disabled enchantments.
     * Cancels the result if:
     * 1. The second item contains a disabled enchantment directly
     * 2. The fusion result would create a disabled enchantment level
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);

        if (firstItem == null || secondItem == null) {
            return;
        }

        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
        if (itemsManager == null) {
            return;
        }

        itemsManager.getCustomItem(firstItem).ifPresent(customItem -> {
            List<DisabledEnchantment> disabledEnchantments = customItem.settings().disabledEnchantments();
            if (disabledEnchantments == null || disabledEnchantments.isEmpty()) {
                return;
            }

            // Check 1: if any disabled enchantment is directly on the second item
            for (DisabledEnchantment disabledEnchantment : disabledEnchantments) {
                if (hasDisabledEnchantment(secondItem, disabledEnchantment)) {
                    event.setResult(null);
                    return;
                }
            }

            // Check 2: Calculate what the fusion result would be and check if it creates disabled enchants
            Map<Enchantment, Integer> firstEnchants = getEnchantments(firstItem);
            Map<Enchantment, Integer> secondEnchants = getEnchantments(secondItem);

            // Simulate vanilla anvil fusion logic
            for (Map.Entry<Enchantment, Integer> secondEntry : secondEnchants.entrySet()) {
                Enchantment enchant = secondEntry.getKey();
                int secondLevel = secondEntry.getValue();

                if (firstEnchants.containsKey(enchant)) {
                    int firstLevel = firstEnchants.get(enchant);
                    int resultLevel;

                    if (firstLevel == secondLevel) {
                        // Same level: upgrade by 1 if possible (vanilla anvil behavior)
                        resultLevel = Math.min(firstLevel + 1, enchant.getMaxLevel());
                    } else {
                        // Different levels: take the higher one
                        resultLevel = Math.max(firstLevel, secondLevel);
                    }

                    // Check if this result level is disabled
                    for (DisabledEnchantment disabledEnchant : disabledEnchantments) {
                        if (disabledEnchant.matches(enchant, resultLevel)) {
                            event.setResult(null);
                            return;
                        }
                    }
                }
            }
        });
    }

    /**
     * Gets all enchantments from an item (regular or book).
     *
     * @param item the item to get enchantments from
     * @return map of enchantments and their levels
     */
    private java.util.Map<Enchantment, Integer> getEnchantments(ItemStack item) {
        if (!item.hasItemMeta()) {
            return Collections.emptyMap();
        }

        ItemMeta meta = item.getItemMeta();

        // Check enchantment storage meta (for books)
        if (meta instanceof EnchantmentStorageMeta enchantMeta) {
            return new HashMap<>(enchantMeta.getStoredEnchants());
        }

        // Regular enchantments
        return new HashMap<>(item.getEnchantments());
    }

    /**
     * Handles enchant events to remove disabled enchantments.
     * Removes any disabled enchantments from the enchants to add.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEnchant(EnchantItemEvent event) {
        ItemStack itemStack = event.getItem();

        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
        if (itemsManager == null) {
            return;
        }

        itemsManager.getCustomItem(itemStack).ifPresent(customItem -> {
            List<DisabledEnchantment> disabledEnchantments = customItem.settings().disabledEnchantments();
            if (disabledEnchantments == null || disabledEnchantments.isEmpty()) {
                return;
            }

            for (DisabledEnchantment disabledEnchantment : disabledEnchantments) {
                if (disabledEnchantment.enchantment() == null) {
                    // Disable all enchantments
                    if (disabledEnchantment.level() == -1) {
                        event.getEnchantsToAdd().clear();
                    } else {
                        // Disable all enchantments at specific level
                        event.getEnchantsToAdd().entrySet().removeIf(entry ->
                                entry.getValue() == disabledEnchantment.level());
                    }
                } else {
                    // Disable specific enchantment
                    if (disabledEnchantment.level() == -1) {
                        event.getEnchantsToAdd().remove(disabledEnchantment.enchantment());
                    } else {
                        // Disable specific enchantment at specific level
                        event.getEnchantsToAdd().entrySet().removeIf(entry ->
                                entry.getKey().equals(disabledEnchantment.enchantment()) &&
                                        entry.getValue() == disabledEnchantment.level());
                    }
                }
            }
        });
    }

    /**
     * Handles prepare enchant table events to remove disabled enchantment offers.
     * Sets offers to null if they match a disabled enchantment.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepareEnchantTable(PrepareItemEnchantEvent event) {
        ItemStack itemStack = event.getItem();

        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
        if (itemsManager == null) {
            return;
        }

        itemsManager.getCustomItem(itemStack).ifPresent(customItem -> {
            List<DisabledEnchantment> disabledEnchantments = customItem.settings().disabledEnchantments();
            if (disabledEnchantments == null || disabledEnchantments.isEmpty()) {
                return;
            }

            EnchantmentOffer[] offers = event.getOffers();
            for (int i = 0; i < offers.length; i++) {
                EnchantmentOffer offer = offers[i];
                if (offer != null) {
                    for (DisabledEnchantment disabledEnchantment : disabledEnchantments) {
                        Enchantment offerEnchant = offer.getEnchantment();
                        int offerLevel = offer.getEnchantmentLevel();

                        if (disabledEnchantment.matches(offerEnchant, offerLevel)) {
                            event.getOffers()[i] = null;
                            break;
                        }
                    }
                }
            }
        });
    }
}
