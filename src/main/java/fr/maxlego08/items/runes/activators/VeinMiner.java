package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneVeinMiningConfiguration;
import fr.maxlego08.items.api.runes.handlers.BreakHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class VeinMiner implements BreakHandler<RuneVeinMiningConfiguration>, RuneActivator {


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
        Queue<Block> blocksToCheck = new LinkedList<>();
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

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneVeinMiningConfiguration configuration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        var player = event.getPlayer();
        var block = event.getBlock();
        var itemStack = player.getInventory().getItemInMainHand();
        if (!configuration.contains(block.getType())) return origin;
        var blocks = this.getVeinBlocks(block, configuration.blockLimit());
        this.isValidTargetBlock(plugin, player, block, origin, configuration);
        blocks.removeIf(veinBlock -> !this.isValidTargetBlock(plugin, player, veinBlock, origin, configuration));
        blocks.removeIf(veinBlock -> triggerBlockBreakEvent(configuration, veinBlock, player));
        blocks.forEach(veinBlock -> drops.put(veinBlock.getLocation(), new ArrayList<>(veinBlock.getDrops(itemStack))));
        return blocks;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
