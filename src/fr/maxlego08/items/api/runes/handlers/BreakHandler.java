package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BreakHandler<T extends RuneConfiguration> {

    Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, T runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops);

    default boolean triggerBlockBreakEvent(T runeConfiguration, Block targetBlock, Player player) {
        if (runeConfiguration.isEventBlockBreakEvent()) {
            CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(targetBlock, player);
            customBlockBreakEvent.callEvent();
            return customBlockBreakEvent.isCancelled();
        }
        return false;
    }

    default boolean isValidTargetBlock(ItemPlugin plugin, Player player, Block targetBlock, Set<Block> origin, T configuration) {
        return targetBlock != null && !origin.contains(targetBlock) && !targetBlock.getType().isAir() && plugin.hasAccess(player, targetBlock.getLocation()) && configuration.contains(targetBlock.getType());
    }

}
