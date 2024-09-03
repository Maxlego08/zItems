package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
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

public interface RuneActivator<T extends RuneConfiguration> {

    void jobsGainExperience(ItemPlugin plugin, JobsExpGainEventWrapper event, T runeConfiguration);

    Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, T runeConfiguration, Set<Block> origin, Map<Location, List<ItemStack>> drops);

    void interactBlock(ItemPlugin plugin, PlayerInteractEvent listener, T runeConfiguration);

    void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, T runeConfiguration) throws Exception;

    int getPriority();
}
