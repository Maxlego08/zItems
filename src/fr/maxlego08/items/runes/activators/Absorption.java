package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.EmptyConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Absorption extends RuneActivatorHelper<EmptyConfiguration> {

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, EmptyConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        Player player = event.getPlayer();
        Map<Location, List<ItemStack>> map = new HashMap<>(drops);
        map.forEach((location, itemStacks) -> {
            var notAdd = player.getInventory().addItem(itemStacks.toArray(new ItemStack[0]));
            drops.put(location, new ArrayList<>(notAdd.values()));
        });
        return origin;
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
