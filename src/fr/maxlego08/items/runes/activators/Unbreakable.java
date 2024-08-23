package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Unbreakable implements RuneActivator<RuneConfiguration> {
    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        return Set.of();
    }

    @Override
    public void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, RuneConfiguration runeConfiguration) {}

    @Override
    public ItemStack applyOnItems(ItemPlugin plugin, ItemStack itemStack, RuneConfiguration runeConfiguration) throws Exception {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
