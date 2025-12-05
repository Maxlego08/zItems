package fr.traqueur.items.effects.handlers;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.events.SpawnerDropEvent;
import fr.traqueur.items.effects.settings.EmptySettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@AutoEffect(value = "SILK_SPAWNER")
public record SilkSpawner(ItemsPlugin plugin) implements EffectHandler.SingleEventEffectHandler<EmptySettings, BlockBreakEvent> {

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        Set<Block> affectedBlocks = new HashSet<>(context.affectedBlocks());
        if(context.affectedBlocks().isEmpty()) {
            Block block = context.getEventAs(eventType()).getBlock();
            affectedBlocks.add(block);
        }

        for (Block block : affectedBlocks) {
            if (block.getType() == Material.SPAWNER) {
                ItemStack itemStack = this.getSpawner(block);

                SpawnerDropEvent event = new SpawnerDropEvent(context.executor(), block.getLocation(), itemStack);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    continue;
                }
                context.addDrop(itemStack);
            }
        }
    }

    private @NotNull ItemStack getSpawner(Block block) {
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        ItemStack itemStack = new ItemStack(Material.SPAWNER);
        EntityType type = spawner.getSpawnedType();
        BlockStateMeta blockStateMeta = (BlockStateMeta) itemStack.getItemMeta();
        CreatureSpawner itemSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
        itemSpawner.setSpawnedType(type);
        blockStateMeta.setBlockState(itemSpawner);
        itemStack.setItemMeta(blockStateMeta);
        return itemStack;
    }

    @Override
    public int priority() {
        return 0;
    }
}
