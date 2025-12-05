package fr.traqueur.items.listeners;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.ItemsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Listener that handles usage restrictions for custom items in various blocks.
 * <p>
 * This listener manages:
 * - Grindstone restrictions (grindstoneEnabled)
 * - Anvil restrictions (anvilEnabled)
 * - Enchanting table restrictions (enchantingTableEnabled)
 * <p>
 * This listener runs at HIGH/HIGHEST priority to block operations before other
 * listeners process them (e.g., before AnvilEffectFusionListener).
 */
public class ItemRestrictionsListener implements Listener {

    private final ItemsPlugin plugin;

    public ItemRestrictionsListener(ItemsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles grindstone events to restrict usage for custom items.
     * <p>
     * The grindstone operation is cancelled if:
     * <ul>
     *     <li>Only one of the two items is a custom item</li>
     *     <li>Both items are custom but have different IDs</li>
     *     <li>Either custom item has grindstoneEnabled set to false</li>
     * </ul>
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onGrindstone(PrepareGrindstoneEvent event) {
        ItemStack upperItem = event.getInventory().getItem(0);
        ItemStack lowerItem = event.getInventory().getItem(1);

        ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
        if (itemsManager == null) {
            return;
        }

        Optional<Item> optionalUpperItem = itemsManager.getCustomItem(upperItem);
        Optional<Item> optionalLowerItem = itemsManager.getCustomItem(lowerItem);

        // If neither item is custom, allow the operation
        if (optionalUpperItem.isEmpty() && optionalLowerItem.isEmpty()) {
            return;
        }

        // If only one item is custom, cancel the operation
        if (optionalUpperItem.isEmpty() || optionalLowerItem.isEmpty()) {
            event.setResult(null);
            Logger.debug("Blocking grindstone: only one item is custom");
            return;
        }

        // Both items are custom - check if they're compatible
        Item upperCustomItem = optionalUpperItem.get();
        Item lowerCustomItem = optionalLowerItem.get();

        // Cancel if items have different IDs or if grindstone is not enabled for either
        if (!upperCustomItem.id().equals(lowerCustomItem.id())
                || !upperCustomItem.settings().grindstoneEnabled()
                || !lowerCustomItem.settings().grindstoneEnabled()) {
            event.setResult(null);
            Logger.debug("Blocking grindstone for items {} and {}: incompatible or disabled",
                    upperCustomItem.id(), lowerCustomItem.id());
        }
    }

    /**
     * Handles anvil events to restrict usage for custom items.
     * <p>
     * The anvil operation is cancelled if either item has anvilEnabled set to false.
     * This runs at HIGHEST priority before AnvilEffectFusionListener.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getItem(0);
        ItemStack secondItem = event.getInventory().getItem(1);

        // If we don't have two items, do nothing
        if (firstItem == null || secondItem == null || firstItem.getType().isAir() || secondItem.getType().isAir()) {
            return;
        }

        ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
        if (itemsManager == null) {
            return;
        }

        // Check first item
        Optional<Item> firstCustomItem = itemsManager.getCustomItem(firstItem);
        if (firstCustomItem.isPresent() && !firstCustomItem.get().settings().anvilEnabled()) {
            event.setResult(null);
            Logger.debug("Blocking anvil usage for item {}: anvil is disabled", firstCustomItem.get().id());
            return;
        }

        // Check second item
        Optional<Item> secondCustomItem = itemsManager.getCustomItem(secondItem);
        if (secondCustomItem.isPresent() && !secondCustomItem.get().settings().anvilEnabled()) {
            event.setResult(null);
            Logger.debug("Blocking anvil usage for item {}: anvil is disabled", secondCustomItem.get().id());
        }
    }

    /**
     * Handles enchanting table events to restrict usage for custom items.
     * <p>
     * The enchanting operation is cancelled if the item has enchantingTableEnabled set to false.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEnchant(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();

        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemsManager itemsManager = plugin.getManager(ItemsManager.class);
        if (itemsManager == null) {
            return;
        }

        Optional<Item> customItem = itemsManager.getCustomItem(item);
        if (customItem.isPresent() && !customItem.get().settings().enchantingTableEnabled()) {
            event.setCancelled(true);
            Logger.debug("Blocking enchanting table usage for item {}: enchanting table is disabled",
                    customItem.get().id());
        }
    }
}