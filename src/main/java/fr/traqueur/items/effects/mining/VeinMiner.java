package fr.traqueur.items.effects.mining;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.annotations.IncompatibleWith;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.DurabilityManager;
import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.effects.mining.VeinMinerSettings;
import fr.traqueur.items.infrastructure.support.EventUtil;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AutoEffect(value = "VEIN_MINING")
@IncompatibleWith(Hammer.class)
public class VeinMiner implements EffectHandler.SingleEventEffectHandler<VeinMinerSettings, BlockBreakEvent> {

    @Override
    public void handle(EffectContext context, VeinMinerSettings settings) {
        BlockBreakEvent event = context.getEventAs(BlockBreakEvent.class);
        Block block = event.getBlock();
        Player player = context.executor();

        // Check if the block can be vein mined
        if (!settings.isBreakable(block)) {
            return;
        }

        // Get all connected blocks of the same type
        Set<Block> veinBlocks = getVeinBlocks(block, settings.blockLimit());

        // Filter blocks that can be broken
        veinBlocks.removeIf(veinBlock -> !isValidTargetBlock(veinBlock, new HashSet<>(), settings, player));

        if (veinBlocks.isEmpty()) {
            return;
        }

        // Cancel the main event to prevent default block break behavior
        event.setCancelled(true);

        int actuallyBrokenBlocks = 0;

        // Process all vein blocks
        for (Block veinBlock : veinBlocks) {
            // Fire BlockBreakEvent for allowed plugins only
            boolean success = EventUtil.fireEvent(new BlockBreakEvent(veinBlock, player), BlockBreakEvent.getHandlerList());
            if (!success) {
                continue; // Skip this block if event was cancelled
            }

            // Add to affected blocks only if not cancelled
            context.affectedBlocks().add(veinBlock);

            // Check if this is a custom block (zItems, ItemsAdder, Nexo, Oraxen, etc.)
            CustomBlockProviderRegistry providerRegistry = Registry.get(CustomBlockProviderRegistry.class);
            Optional<List<ItemStack>> customDrop = providerRegistry.getCustomBlockDrop(veinBlock, player);

            if (customDrop.isPresent()) {
                // Custom block found - add custom item drop
                context.addDrops(customDrop.get());
            } else {
                // Normal block - use vanilla drops
                Collection<ItemStack> blockDrops = veinBlock.getDrops(context.itemSource());
                context.addDrops(blockDrops);
            }

            actuallyBrokenBlocks++;
        }

        // Apply damage based on actually broken blocks
        int damage = settings.damage() == -1 ? actuallyBrokenBlocks : settings.damage();
        if (damage > 0) {
            JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(context.itemSource(), damage, player);
        }
    }

    /**
     * Retrieves a set of connected blocks of the same type as the given starting block,
     * up to a specified maximum vein size. The method explores adjacent blocks in all
     * directions (including diagonals and different heights) to form a vein.
     *
     * @param startBlock  the initial block to begin the vein search
     * @param maxVeinSize the maximum number of blocks to include in the vein
     * @return a set of blocks forming the vein, including the starting block
     */
    private Set<Block> getVeinBlocks(Block startBlock, int maxVeinSize) {
        Set<Block> veinBlocks = new HashSet<>();
        Queue<Block> blocksToCheck = new ArrayDeque<>();
        Material blockType = startBlock.getType();

        blocksToCheck.add(startBlock);

        while (!blocksToCheck.isEmpty() && veinBlocks.size() < maxVeinSize) {
            Block currentBlock = blocksToCheck.poll();

            if (currentBlock.getType() != blockType || veinBlocks.contains(currentBlock)) {
                continue;
            }

            veinBlocks.add(currentBlock);

            // Explore the 26 positions around the current block (including diagonals and heights)
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;  // Ignore the current block itself

                        Block adjacentBlock = currentBlock.getRelative(x, y, z);

                        // Add only the blocks not already visited
                        if (!veinBlocks.contains(adjacentBlock)) {
                            blocksToCheck.add(adjacentBlock);
                        }
                    }
                }
            }
        }

        return veinBlocks;
    }

    private boolean isValidTargetBlock(Block targetBlock, Set<Block> alreadyProcessed, VeinMinerSettings settings, Player player) {
        if (alreadyProcessed.contains(targetBlock)) {
            return false;
        }

        // Check if player has permission to break block at this location
        if (!EventUtil.canBreakBlock(player, targetBlock.getLocation())) {
            return false;
        }

        return settings.isBreakable(targetBlock);
    }

    @Override
    public int priority() {
        return 1;
    }
}