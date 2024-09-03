package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RunePipeline {

    private final RuneManager runeManager;
    private final List<Rune> runes;

    public RunePipeline(RuneManager runeManager, List<Rune> activators) {
        this.runeManager = runeManager;
        activators.sort(Comparator.comparingInt(rune -> rune.getType().getActivator().getPriority()));
        this.runes = activators.reversed();
    }

    private void handleBreak(ItemPlugin plugin, BlockBreakEvent event) {
        Map<Location, List<ItemStack>> drops = new HashMap<>();
        Set<Block> blocks = breakBlocks(plugin, event, drops);
        if (blocks.isEmpty()) return;

        event.setDropItems(false);

        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
        drops.forEach((location, itemStacks) -> itemStacks.forEach(itemStack1 -> location.getWorld().dropItemNaturally(location, itemStack1)));
    }

    private Set<Block> breakBlocks(ItemPlugin plugin, BlockBreakEvent event, Map<Location, List<ItemStack>> drops) {
        Set<Block> currentBlocks = new HashSet<>();
        currentBlocks.add(event.getBlock());
        drops.put(event.getBlock().getLocation(), new ArrayList<>(event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand())));

        for (Rune rune : runes) {
            currentBlocks = new HashSet<>(rune.getType().getActivator().breakBlocks(plugin, event, rune.getConfiguration(), new HashSet<>(currentBlocks), drops));
        }
        return currentBlocks;
    }

    public <T extends Event> void pipeline(ItemPlugin plugin, T event) {
        switch (event) {
            case PlayerInteractEvent playerInteractEvent ->  {
                for (Rune rune : runes) {
                    rune.getType().getActivator().interactBlock(plugin, playerInteractEvent, rune.getConfiguration());
                }
            }
            case JobsExpGainEventWrapper jobsExpGainEventWrapper -> {
                for (Rune rune : runes) {
                    rune.getType().getActivator().jobsGainExperience(plugin, jobsExpGainEventWrapper, rune.getConfiguration());
                }
            }
            case JobsPayementEventWrapper jobsPayementEventWrapper -> {
                for (Rune rune : runes) {
                    rune.getType().getActivator().jobsGainMoney(plugin, jobsPayementEventWrapper, rune.getConfiguration());
                }
            }
            case BlockBreakEvent blockBreakEvent -> {
                handleBreak(plugin, blockBreakEvent);
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

}
