package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.handlers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RunePipeline {

    private final List<Rune> runes;

    public RunePipeline(List<Rune> activators) {
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

        for (Rune rune : runes.stream().filter(rune -> rune.getType().getActivator() instanceof BreakHandler<?>).toList()) {
            currentBlocks = new HashSet<>(((BreakHandler<?>) rune.getType().getActivator()).breakBlocks(plugin, event, rune.getConfiguration(), new HashSet<>(currentBlocks), drops));
        }
        return currentBlocks;
    }

    public <T extends Event> void pipeline(ItemPlugin plugin, T event) {
        switch (event) {
            case PlayerInteractEvent playerInteractEvent ->  {
                for (Rune rune : runes.stream().filter(rune -> rune.getType().getActivator() instanceof InteractionHandler<?>).toList()) {
                    ((InteractionHandler<?>) rune.getType().getActivator()).interactBlock(plugin, playerInteractEvent, rune.getConfiguration());
                }
            }
            case JobsExpGainEventWrapper jobsExpGainEventWrapper -> {
                for (Rune rune : runes.stream().filter(rune -> rune.getType().getActivator() instanceof JobsExperienceHandler<?>).toList()) {
                    ((JobsExperienceHandler<?>) rune.getType().getActivator()).jobsGainExperience(plugin, jobsExpGainEventWrapper, rune.getConfiguration());
                }
            }
            case JobsPayementEventWrapper jobsPayementEventWrapper -> {
                for (Rune rune : runes.stream().filter(rune -> rune.getType().getActivator() instanceof JobsMoneyHandler<?>).toList()) {
                    ((JobsMoneyHandler<?>) rune.getType().getActivator()).jobsGainMoney(plugin, jobsPayementEventWrapper, rune.getConfiguration());
                }
            }
            case BlockBreakEvent blockBreakEvent -> {
                handleBreak(plugin, blockBreakEvent);
            }
            case EntityDeathEvent entityDeathEvent -> {
                for (Rune rune : runes.stream().filter(rune -> rune.getType().getActivator() instanceof EntityDeathHandler<?>).toList()) {
                    ((EntityDeathHandler<?>) rune.getType().getActivator()).onEntityDeath(plugin, entityDeathEvent, rune.getConfiguration());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

}
