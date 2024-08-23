package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneMeltMiningConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;

public class MeltMining implements RuneActivator<RuneMeltMiningConfiguration> {

    private final Map<Material, FurnaceRecipe> caches = new HashMap<>();

    public Set<Block> meltBlocks(BlockBreakEvent event, Set<Block> blocks, ItemStack itemStack, Map<Location, List<ItemStack>> drops) {
        float totalExperience = 0;

        for (Block block : blocks) {
            Location location = block.getLocation().add(0.5, 0.5, 0.5);
            World world = block.getWorld();

            for (ItemStack blockDrop : block.getDrops(itemStack)) {
                Optional<FurnaceRecipe> optionalRecipe = getFurnaceRecipeFor(blockDrop.getType());
                if (optionalRecipe.isEmpty()) continue;

                FurnaceRecipe recipe = optionalRecipe.get();
                totalExperience += recipe.getExperience();
                drops.put(block.getLocation(), Collections.singletonList(recipe.getResult().asQuantity(blockDrop.getAmount())));
            }

            spawnFlameParticles(world, location);
        }

        event.setExpToDrop(Math.max(1, (int) totalExperience));
        return blocks;
    }

    private Optional<FurnaceRecipe> getFurnaceRecipeFor(Material blockType) {
        return Optional.ofNullable(caches.computeIfAbsent(blockType, this::findFurnaceRecipe));
    }

    private FurnaceRecipe findFurnaceRecipe(Material blockType) {
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

    private void spawnFlameParticles(World world, Location location) {
        world.spawnParticle(Particle.FLAME, location, 5, 0.3, 0.3, 0.3, 0.02);
    }

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneMeltMiningConfiguration configuration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        return this.meltBlocks(event, origin, itemStack, drops);
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, RuneMeltMiningConfiguration farmingHoeConfiguration) {}

    @Override
    public void applyOnItems(ItemsPlugin plugin, ItemStack itemStack, RuneMeltMiningConfiguration runeConfiguration) {}

    @Override
    public int getPriority() {
        return 0;
    }
}
