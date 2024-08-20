package fr.maxlego08.items.runes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MeltMining {

    private static final Map<Material, FurnaceRecipe> caches = new HashMap<>();

    public static void meltBlocks(BlockBreakEvent event, Block block, ItemStack itemStack) {
        meltBlocks(event, Set.of(block), itemStack);
    }

    public static void meltBlocks(BlockBreakEvent event, Set<Block> blocks, ItemStack itemStack) {
        float totalExperience = 0;

        for (Block block : blocks) {
            Location location = block.getLocation().add(0.5, 0.5, 0.5);
            World world = block.getWorld();
            boolean hasProcessedDrop = false;

            for (ItemStack blockDrop : block.getDrops(itemStack)) {
                Optional<FurnaceRecipe> optionalRecipe = getFurnaceRecipeFor(blockDrop.getType());
                if (optionalRecipe.isEmpty()) continue;

                FurnaceRecipe recipe = optionalRecipe.get();
                totalExperience += recipe.getExperience();
                world.dropItemNaturally(location, recipe.getResult().asQuantity(blockDrop.getAmount()));
                hasProcessedDrop = true;
            }

            if (!hasProcessedDrop) {
                block.breakNaturally(itemStack);
            } else if (!block.equals(event.getBlock())) {
                block.setType(Material.AIR);
            }

            spawnFlameParticles(world, location);
        }

        event.setExpToDrop(Math.max(1, (int) totalExperience));
        event.setDropItems(false);
    }

    private static Optional<FurnaceRecipe> getFurnaceRecipeFor(Material blockType) {
        return Optional.ofNullable(caches.computeIfAbsent(blockType, MeltMining::findFurnaceRecipe));
    }

    private static FurnaceRecipe findFurnaceRecipe(Material blockType) {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof FurnaceRecipe furnaceRecipe) {
                RecipeChoice choice = furnaceRecipe.getInputChoice();
                if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                    if (materialChoice.getChoices().contains(blockType)) {
                        return furnaceRecipe;
                    }
                }
            }
        }
        return null;
    }

    private static void spawnFlameParticles(World world, Location location) {
        world.spawnParticle(Particle.FLAME, location, 5, 0.3, 0.3, 0.3, 0.02);
    }
}
