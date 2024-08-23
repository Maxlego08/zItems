package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RuneActivator<T extends RuneConfiguration> {

    Set<Block> breakBlocks(ItemsPlugin plugin,
                           BlockBreakEvent event, T runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops);

    void interactBlock(ItemsPlugin plugin,
                       PlayerInteractEvent listener, T runeConfiguration);

    void applyOnItems(ItemsPlugin plugin, ItemStack itemStack, T runeConfiguration);

    int getPriority();
}
