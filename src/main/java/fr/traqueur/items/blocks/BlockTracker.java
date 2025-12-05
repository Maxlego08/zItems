package fr.traqueur.items.blocks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.blocks.TrackedBlock;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.items.utils.ChunkUtil;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

/**
 * Tracks blocks placed from custom items to restore the correct item on break.
 * <p>
 * Uses chunk PDC (PersistentDataContainer) for persistence and in-memory cache for performance.
 * This allows custom items to be properly dropped when their placed blocks are broken.
 * <p>
 * Architecture:
 * - Memory: Guava Table&lt;WorldChunkKey, Integer, String&gt; where WorldChunkKey combines world UUID and chunk coordinates
 * - Persistence: Chunk PDC stores List&lt;TrackedBlock&gt; using custom TrackedBlockDataType
 * - Load/Unload: Chunks are loaded into cache on ChunkLoadEvent and saved on ChunkUnloadEvent
 */
public class BlockTracker {

    private static final BlockTracker INSTANCE = new BlockTracker();

    public static BlockTracker get() {
        return INSTANCE;
    }

    /**
     * In-memory cache using Guava Table for efficient chunk-based lookups.
     * Row: WorldChunkKey (world UUID + chunk key) - unique identifier for each chunk across all worlds
     * Column: Packed block position (int) - position within the chunk
     * Value: Custom item ID (String)
     */
    private final Table<WorldChunkKey, Integer, String> cache;

    private BlockTracker() {
        this.cache = HashBasedTable.create();
    }

    /**
     * Composite key that uniquely identifies a chunk across all worlds.
     * Combines world UUID with chunk coordinates to prevent collisions between
     * chunks at the same X,Z position in different worlds (Overworld, Nether, End).
     */
    private record WorldChunkKey(UUID worldUid, long chunkKey) {
        WorldChunkKey(Chunk chunk) {
            this(chunk.getWorld().getUID(), ChunkUtil.getChunkKey(chunk));
        }
    }

    /**
     * Tracks a block as being placed from a custom item.
     *
     * @param block the block that was placed
     * @param itemId the custom item ID
     */
    public void trackBlock(Block block, String itemId) {
        WorldChunkKey worldChunkKey = new WorldChunkKey(block.getChunk());
        int packedPosition = packBlockPosition(block);

        cache.put(worldChunkKey, packedPosition, itemId);

        Logger.debug("Tracked block at {} with item ID: {}", formatBlockLocation(block), itemId);
    }

    /**
     * Gets the custom item ID for a block, if it's tracked.
     *
     * @param block the block to check
     * @return Optional containing the item ID, or empty if not tracked
     */
    public Optional<String> getTrackedItemId(Block block) {
        WorldChunkKey worldChunkKey = new WorldChunkKey(block.getChunk());
        int packedPosition = packBlockPosition(block);

        String itemId = cache.get(worldChunkKey, packedPosition);
        return Optional.ofNullable(itemId);
    }

    /**
     * Gets the custom ItemStack for a tracked block without untracking it.
     * This should be called by handlers that break multiple blocks (Hammer, VeinMiner)
     * to ensure custom blocks drop the correct item.
     *
     * <p>The caller is responsible for calling {@link #untrackBlock(Block)} after
     * successfully breaking the block to prevent memory leaks.
     *
     * @param block the block to check
     * @param player the player breaking the block (used for item building context)
     * @return Optional containing the custom ItemStack, or empty if not tracked
     */
    public Optional<ItemStack> getCustomBlockDrop(Block block, Player player) {
        Optional<String> itemId = getTrackedItemId(block);

        if (itemId.isEmpty()) {
            return Optional.empty();
        }

        // Get the custom item from registry
        var itemsRegistry = fr.traqueur.items.api.registries.Registry.get(
            fr.traqueur.items.api.registries.ItemsRegistry.class
        );
        var customItem = itemsRegistry.getById(itemId.get());

        if (customItem == null) {
            Logger.debug("Tracked block at {} has invalid item ID: {}",
                formatBlockLocation(block), itemId.get());
            return Optional.empty();
        }

        // Build and return the custom item
        ItemStack customItemStack = customItem.build(player, 1);
        Logger.debug("Retrieved custom block drop at {}: {}",
            formatBlockLocation(block), itemId.get());

        return Optional.of(customItemStack);
    }

    /**
     * Untracks a block (removes from tracking system).
     * Should be called when a tracked block is broken.
     *
     * @param block the block to untrack
     */
    public void untrackBlock(Block block) {
        WorldChunkKey worldChunkKey = new WorldChunkKey(block.getChunk());
        int packedPosition = packBlockPosition(block);

        String removed = cache.remove(worldChunkKey, packedPosition);
        if (removed != null) {
            Logger.debug("Untracked block at {}", formatBlockLocation(block));
        }
    }

    /**
     * Loads tracked blocks from chunk PDC into memory cache.
     * Called on ChunkLoadEvent.
     *
     * @param chunk the chunk being loaded
     */
    public void loadChunk(Chunk chunk) {
        WorldChunkKey worldChunkKey = new WorldChunkKey(chunk);
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();

        List<TrackedBlock> trackedBlocks = Keys.TRACKED_BLOCKS.get(pdc, new ArrayList<>());

        if (!trackedBlocks.isEmpty()) {
            for (TrackedBlock trackedBlock : trackedBlocks) {
                cache.put(worldChunkKey, trackedBlock.packedPosition(), trackedBlock.itemId());
            }

            Logger.debug("Loaded {} tracked blocks from chunk {} in world {}",
                trackedBlocks.size(), ChunkUtil.getChunkKey(chunk), chunk.getWorld().getName());
        }
    }

    /**
     * Saves tracked blocks from memory cache to chunk PDC.
     * Called on ChunkUnloadEvent.
     *
     * @param chunk the chunk being unloaded
     */
    public void unloadChunk(Chunk chunk) {
        WorldChunkKey worldChunkKey = new WorldChunkKey(chunk);
        Map<Integer, String> chunkData = cache.row(worldChunkKey);

        PersistentDataContainer pdc = chunk.getPersistentDataContainer();

        if (!chunkData.isEmpty()) {
            // Convert map to list of TrackedBlock
            List<TrackedBlock> trackedBlocks = new ArrayList<>(chunkData.size());
            for (Map.Entry<Integer, String> entry : chunkData.entrySet()) {
                trackedBlocks.add(new ZTrackedBlock(entry.getKey(), entry.getValue()));
            }

            Keys.TRACKED_BLOCKS.set(pdc, trackedBlocks);

            Logger.debug("Saved {} tracked blocks for chunk {} in world {}",
                trackedBlocks.size(), ChunkUtil.getChunkKey(chunk), chunk.getWorld().getName());

            // Remove from cache after saving
            cache.row(worldChunkKey).clear();
        } else {
            // Remove PDC data if no blocks are tracked (cleanup)
            pdc.remove(Keys.TRACKED_BLOCKS.getNamespacedKey());
        }
    }

    /**
     * Clears all tracked blocks from memory cache.
     * Does NOT affect persistent data in chunk PDC.
     */
    public void clearCache() {
        int totalBlocks = cache.size();
        int totalChunks = cache.rowKeySet().size();
        cache.clear();
        Logger.info("Cleared block tracker cache ({} chunks, {} blocks)", totalChunks, totalBlocks);
    }

    /**
     * Packs block coordinates relative to chunk into a single integer using bit shifting.
     * <p>
     * This method encodes the 3D position (x, y, z) within a chunk into a single 32-bit integer.
     * <p>
     * <b>Bit Layout (32 bits total):</b>
     * <pre>
     * Bits 0-8   (9 bits):  Y coordinate (0-511, supports -64 to 319 when offset by +64)
     * Bits 9-12  (4 bits):  Z coordinate (0-15, relative to chunk)
     * Bits 13-16 (4 bits):  X coordinate (0-15, relative to chunk)
     * Bits 17-31 (unused): Always 0
     * </pre>
     * <p>
     * <b>Why no collisions are possible:</b>
     * <ul>
     *   <li>Each coordinate occupies separate, non-overlapping bits</li>
     *   <li>X uses bits 13-16 (0xF000 mask when shifted)</li>
     *   <li>Z uses bits 9-12 (0x0F00 mask when shifted)</li>
     *   <li>Y uses bits 0-8 (0x01FF mask)</li>
     *   <li>Total: 4 + 4 + 9 = 17 bits used out of 32 available</li>
     * </ul>
     * <p>
     * <b>Example:</b>
     * Block at chunk-relative position (5, 100, 10):
     * <pre>
     * x = 5:     0000 0101
     * z = 10:    0000 1010
     * y = 164:   1010 0100 (100 + 64 offset)
     *
     * Packed: 0001 0100 1010 1010 0100
     *         ^^^^ ^^^^ ^^^^ ^^^^ ^^^^
     *         x    z    y
     * Result: 0x14A4 = 5,284
     * </pre>
     *
     * @param block The block to pack
     * @return Packed position as a single integer (guaranteed unique for each position in chunk)
     */
    private int packBlockPosition(Block block) {
        // Extract chunk-relative coordinates (0-15 for x and z)
        int x = block.getX() & 0xF;  // Mask with 0xF (0000 1111) to keep only last 4 bits
        int z = block.getZ() & 0xF;  // Mask with 0xF (0000 1111) to keep only last 4 bits

        // Convert Y from [-64, 319] to [0, 383], then mask to 9 bits
        // Adding 64 offset: -64 becomes 0, 319 becomes 383
        int y = (block.getY() + 64) & 0x1FF; // Mask with 0x1FF (1 1111 1111) to keep 9 bits

        // Combine using bit shifting:
        // x << 13: Move x to bits 13-16
        // z << 9:  Move z to bits 9-12
        // y:       Already in bits 0-8
        // OR (|) combines them without overlap
        return (x << 13) | (z << 9) | y;
    }

    /**
     * Formats a block location for debug logging.
     *
     * @param block the block to format
     * @return formatted location string
     */
    private String formatBlockLocation(Block block) {
        return String.format("%s:%d,%d,%d",
            block.getWorld().getName(),
            block.getX(),
            block.getY(),
            block.getZ()
        );
    }
}