package fr.traqueur.items.blocks;

import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.managers.ItemsManager;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Listener that handles block tracking for custom items.
 * <p>
 * - Tracks blocks when placed from custom items
 * - Drops correct custom item when tracked blocks are broken
 * - Manages chunk load/unload for persistence
 */
public record BlockTrackerListener(BlockTracker tracker, ItemsManager itemsManager,
                                   EffectsManager effectsManager) implements Listener {

    /**
     * Tracks blocks placed from custom items.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemInHand = event.getItemInHand();

        // Check if the placed block came from a custom item
        Optional<Item> customItem = itemsManager.getCustomItem(itemInHand);
        if (customItem.isPresent()) {
            Item item = customItem.get();

            // Only track the block if the item is configured as trackable
            if (item.settings().trackable()) {
                Block placedBlock = event.getBlockPlaced();
                tracker.trackBlock(placedBlock, item.id());
            }
        }
    }

    /**
     * Handles block break events for tracked blocks when using normal tools.
     * Drops the correct custom item instead of the default block drops.
     *
     * <p>If the player is using a custom item with effects (like Hammer or VeinMiner),
     * this listener does NOT cancel the event - it lets the effect handlers manage
     * the custom block drops through the CustomBlockProviderRegistry.
     */
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Optional<String> trackedItemId = tracker.getTrackedItemId(block);

        if (trackedItemId.isEmpty()) {
            return; // Not a tracked custom block
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Check if the player is using an item with effects
        if (effectsManager.hasEffects(itemInHand)) {
            // Let the effect handlers (Hammer, VeinMiner, etc.) manage the custom block drops
            // They will use CustomBlockProviderRegistry to handle it properly
            return;
        }

        // Player is using a normal tool - handle the custom block drop here
        ItemsRegistry itemsRegistry = Registry.get(ItemsRegistry.class);
        Item customItem = itemsRegistry.getById(trackedItemId.get());

        if (customItem != null) {
            // Cancel default drops
            event.setCancelled(true);
            // Drop the custom item instead
            ItemStack customItemStack = customItem.build(player, 1);
            // Drop custom item at block location
            block.getWorld().dropItemNaturally(block.getLocation(), customItemStack);

            // Untrack the block
            tracker.untrackBlock(block);
            block.setType(Material.AIR);
        }
    }

    /**
     * Loads tracked blocks into cache when a chunk loads.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        tracker.loadChunk(event.getChunk());
    }

    /**
     * Saves tracked blocks from cache when a chunk unloads.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        tracker.unloadChunk(event.getChunk());
    }
}