package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface RuneActivator<T extends RuneConfiguration> {

    void breakBlocks(ItemsPlugin plugin,
                     BlockBreakEvent event, T farmingHoeConfiguration);

    void interactBlock(ItemsPlugin plugin,
                       PlayerInteractEvent listener, T farmingHoeConfiguration);
}
