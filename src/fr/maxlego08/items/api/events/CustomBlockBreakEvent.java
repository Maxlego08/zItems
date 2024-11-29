package fr.maxlego08.items.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class CustomBlockBreakEvent extends BlockBreakEvent {

    public CustomBlockBreakEvent(Block theBlock, Player player) {
        super(theBlock, player);
    }
}
