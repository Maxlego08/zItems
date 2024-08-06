package fr.maxlego08.items.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class FarmingHoeBlockBreakEvent extends BlockBreakEvent {

    public FarmingHoeBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }
}
