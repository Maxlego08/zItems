package fr.maxlego08.items.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBlockBreakEvent extends BlockBreakEvent {

    public CustomBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }
}
