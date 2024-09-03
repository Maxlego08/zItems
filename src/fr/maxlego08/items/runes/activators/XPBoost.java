package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneXPBoostConfiguration;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class XPBoost extends RuneActivatorHelper<RuneXPBoostConfiguration> {

    @Override
    public void onEntityDeath(ItemPlugin plugin, EntityDeathEvent event, RuneXPBoostConfiguration runeConfiguration) {
        int exp = event.getDroppedExp();
        event.setDroppedExp((int) (exp * runeConfiguration.getXpBoost()));
    }

    @Override
    public Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, RuneXPBoostConfiguration runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops) {
        int exp = event.getExpToDrop();
        event.setExpToDrop((int) (exp * runeConfiguration.getXpBoost()));
        return origin;
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
