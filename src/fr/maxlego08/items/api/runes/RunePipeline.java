package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
        drops.put(event.getBlock().getLocation(), new ArrayList<>(event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand())));

        for (Rune rune : runes) {
            currentBlocks = new HashSet<>(rune.getType().getActivator().breakBlocks(plugin, event, rune.getConfiguration(), new HashSet<>(currentBlocks), drops));
        }

        return currentBlocks;
    }

    public void interactBlock(ItemsPlugin plugin, PlayerInteractEvent event) {
        for (Rune rune : runes) {
            rune.getType().getActivator().interactBlock(plugin, event, rune.getConfiguration());
        }
    }

    public void jobsGainExp(ItemsPlugin plugin, JobsExpGainEventWrapper event) {
        for (Rune rune : runes) {
            rune.getType().getActivator().jobsGainExperience(plugin, event, rune.getConfiguration());
        }
    }

    public void jobsGainMoney(ItemsPlugin plugin, JobsPayementEventWrapper event) {
        for (Rune rune : runes) {
            rune.getType().getActivator().jobsGainMoney(plugin, event, rune.getConfiguration());
        }
    }

}
