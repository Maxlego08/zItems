package fr.traqueur.items.effects.handlers;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.settings.EmptySettings;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.*;

@AutoEffect(value = "MELT_MINING")
public class MeltMining implements EffectHandler.SingleEventEffectHandler<EmptySettings, BlockBreakEvent> {

    private static final Map<Material, FurnaceRecipe> CACHES = new HashMap<>();

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        float totalExperience = 0;
        BlockBreakEvent event = context.getEventAs(BlockBreakEvent.class);
        Set<Block> affectedBlocks = new HashSet<>(context.affectedBlocks());
        if(affectedBlocks.isEmpty()) {
            affectedBlocks.add(event.getBlock());
        }

        for (Block block : affectedBlocks) {
            Location location = block.getLocation().add(0.5, 0.5, 0.5);
            World world = block.getWorld();
            for (ItemStack blockDrop : block.getDrops(context.itemSource())) {
                Optional<FurnaceRecipe> optionalRecipe = getFurnaceRecipeFor(blockDrop.getType());
                if (optionalRecipe.isEmpty()) continue;

                FurnaceRecipe recipe = optionalRecipe.get();
                totalExperience += recipe.getExperience();
                ItemStack result = recipe.getResult();
                result.setAmount(blockDrop.getAmount());
                context.addDrop(result);
            }
            this.spawnFlameParticles(world, location);
        }
        event.setExpToDrop((int) totalExperience);
    }

    private void spawnFlameParticles(World world, Location location) {
        world.spawnParticle(Particle.FLAME, location, 5, 0.3, 0.3, 0.3, 0.02);
    }

    @Override
    public int priority() {
        return 0;
    }

    private Optional<FurnaceRecipe> getFurnaceRecipeFor(Material blockType) {
        return Optional.ofNullable(CACHES.computeIfAbsent(blockType, this::findFurnaceRecipe));
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
}
