package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneVeinMiningConfiguration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VeinMiner implements RuneActivator<RuneVeinMiningConfiguration> {

    /**
     * Cette méthode prend un bloc de départ et renvoie un ensemble de tous les blocs connectés du même type.
     *
     * @param startBlock  Le bloc de départ
     * @param maxVeinSize La taille maximale de la veine
     * @return Un ensemble de blocs connectés du même type
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

            // Explore les 26 positions autour du bloc courant (y compris les diagonales et les hauteurs)
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;  // Ignore le bloc courant lui-même

                        Block adjacentBlock = currentBlock.getRelative(x, y, z);

                        // Ajoute seulement les blocs non déjà visités
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
        blocks.removeIf(veinBlock -> !plugin.hasAccess(player, veinBlock.getLocation()) || !configuration.contains(veinBlock.getType()));
        blocks.forEach(veinBlock -> {
            drops.put(veinBlock.getLocation(), new ArrayList<>(veinBlock.getDrops(itemStack)));
        });
        return blocks;
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, RuneVeinMiningConfiguration farmingHoeConfiguration) {}

    @Override
    public void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, RuneVeinMiningConfiguration runeConfiguration) {}

    @Override
    public int getPriority() {
        return 1;
    }
}
