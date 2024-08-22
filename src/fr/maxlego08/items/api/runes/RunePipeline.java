package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.ItemsPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RunePipeline {

    private final List<Rune> runes;

    public RunePipeline(List<Rune> activators) {
        activators.sort(Comparator.comparingInt(rune -> rune.getType().getActivator().getPriority()));
        this.runes = activators.reversed();
    }


    public Set<Block> breakBlocks(ItemsPlugin plugin, BlockBreakEvent event, Map<Location, List<ItemStack>> drops) {
        Set<Block> currentBlocks = new HashSet<>();
        currentBlocks.add(event.getBlock());

        for (Rune rune : runes) {
            currentBlocks = new HashSet<>(rune.getType().getActivator().breakBlocks(plugin, event, rune.getConfiguration(), new HashSet<>(currentBlocks), drops));
        }

        return currentBlocks;
    }

}
