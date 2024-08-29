package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.EmptyConfiguration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.spawner.Spawner;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SilkSpawner extends RuneActivatorHelper<EmptyConfiguration> {

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, EmptyConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        origin.stream().filter(block -> block.getType() == Material.SPAWNER).forEach(block -> {
            CreatureSpawner spawner = (CreatureSpawner) block.getState();
            ItemStack item = new ItemStack(Material.SPAWNER);
            EntityType type = spawner.getSpawnedType();
            BlockStateMeta blockStateMeta = (BlockStateMeta) item.getItemMeta();
            CreatureSpawner itemSpawner = (CreatureSpawner) blockStateMeta.getBlockState();
            itemSpawner.setSpawnedType(type);
            blockStateMeta.setBlockState(itemSpawner);
            item.setItemMeta(blockStateMeta);
            drops.get(block.getLocation()).add(item);
        });
        return origin;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
