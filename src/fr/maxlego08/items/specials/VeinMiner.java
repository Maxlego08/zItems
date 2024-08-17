package fr.maxlego08.items.specials;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class VeinMiner {

    /**
     * Cette méthode prend un bloc de départ et renvoie un ensemble de tous les blocs connectés du même type.
     *
     * @param startBlock  Le bloc de départ
     * @param maxVeinSize La taille maximale de la veine
     * @return Un ensemble de blocs connectés du même type
     */
    public static Set<Block> getVeinBlocks(Block startBlock, int maxVeinSize) {
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
}
