package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneEnchantApplicatorConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantApplicator implements RuneActivator<RuneEnchantApplicatorConfiguration> {

    @Override
    public Set<Block> breakBlocks(ItemsPlugin plugin, BlockBreakEvent event, RuneEnchantApplicatorConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        return Set.of();
    }

    @Override
    public void interactBlock(ItemsPlugin plugin, PlayerInteractEvent listener, RuneEnchantApplicatorConfiguration runeConfiguration) {}

    @Override
    public void applyOnItems(ItemsPlugin plugin, ItemStack itemStack, RuneEnchantApplicatorConfiguration runeConfiguration) {

    }

    @Override
    public int getPriority() {
        return 0;
    }
}
