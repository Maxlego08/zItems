package fr.maxlego08.items.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.handlers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RunePipeline {

    private final List<Rune> runes;
    // Map containing runes filtered by handler type for performance
    private final Map<Class<?>, List<Rune>> runesByHandler;

    // List of all handler types to initialize
    private static final List<Class<?>> HANDLER_TYPES = List.of(
            BreakHandler.class,
            InventorySlotChangeHandler.class,
            InteractionHandler.class,
            JobsExperienceHandler.class,
            JobsMoneyHandler.class,
            EntityDeathHandler.class,
            BucketHandler.class
    );

    public RunePipeline(List<Rune> activators) {
        activators.sort(Comparator.comparingInt(rune -> rune.getType().getActivator().getPriority()));
        this.runes = activators.reversed();

        // Pre-filter runes by handler type once during initialization
        this.runesByHandler = new HashMap<>();
        for (Class<?> handlerType : HANDLER_TYPES) {
            List<Rune> filteredRunes = runes.stream()
                    .filter(rune -> handlerType.isInstance(rune.getType().getActivator()))
                    .toList();
            runesByHandler.put(handlerType, filteredRunes);
        }
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
        List<Rune> breakHandlerRunes = runesByHandler.get(BreakHandler.class);
        if (breakHandlerRunes.isEmpty()) return new HashSet<>();

        Set<Block> currentBlocks = new HashSet<>();
        currentBlocks.add(event.getBlock());
        drops.put(event.getBlock().getLocation(), new ArrayList<>(event.getBlock().getDrops(event.getPlayer().getInventory().getItemInMainHand())));

        for (Rune rune : breakHandlerRunes) {
            currentBlocks = new HashSet<>(((BreakHandler<?>) rune.getType().getActivator()).breakBlocks(plugin, event, rune.getConfiguration(), new HashSet<>(currentBlocks), drops));
        }
        return currentBlocks;
    }

    public void pipeline(ItemPlugin plugin, Player player, InventorySlotChangeHandler.InventorySlotChangeType type) {
        List<Rune> inventorySlotChangeRunes = runesByHandler.get(InventorySlotChangeHandler.class);
        for (Rune rune : inventorySlotChangeRunes) {
            if (type == ((InventorySlotChangeHandler<?>) rune.getType().getActivator()).getType(rune.getConfiguration())) {
                ((InventorySlotChangeHandler<?>) rune.getType().getActivator()).onInventorySlotChange(plugin, player, rune.getConfiguration());
            }
        }
    }

    public <T extends Event> void pipeline(ItemPlugin plugin, T event) {
        switch (event) {
            case PlayerInteractEvent playerInteractEvent -> {
                List<Rune> interactionHandlerRunes = runesByHandler.get(InteractionHandler.class);
                for (Rune rune : interactionHandlerRunes) {
                    ((InteractionHandler<?>) rune.getType().getActivator()).interactBlock(plugin, playerInteractEvent, rune.getConfiguration());
                }
            }
            case JobsExpGainEventWrapper jobsExpGainEventWrapper -> {
                List<Rune> jobsExperienceRunes = runesByHandler.get(JobsExperienceHandler.class);
                for (Rune rune : jobsExperienceRunes) {
                    ((JobsExperienceHandler<?>) rune.getType().getActivator()).jobsGainExperience(plugin, jobsExpGainEventWrapper, rune.getConfiguration());
                }
            }
            case JobsPayementEventWrapper jobsPayementEventWrapper -> {
                List<Rune> jobsMoneyRunes = runesByHandler.get(JobsMoneyHandler.class);
                for (Rune rune : jobsMoneyRunes) {
                    ((JobsMoneyHandler<?>) rune.getType().getActivator()).jobsGainMoney(plugin, jobsPayementEventWrapper, rune.getConfiguration());
                }
            }
            case BlockBreakEvent blockBreakEvent -> handleBreak(plugin, blockBreakEvent);
            case EntityDeathEvent entityDeathEvent -> {
                List<Rune> entityDeathRunes = runesByHandler.get(EntityDeathHandler.class);
                for (Rune rune : entityDeathRunes) {
                    ((EntityDeathHandler<?>) rune.getType().getActivator()).onEntityDeath(plugin, entityDeathEvent, rune.getConfiguration());
                }
            }
            case PlayerBucketEmptyEvent bucketEmptyEvent -> {
                List<Rune> bucketHandlerRunes = runesByHandler.get(BucketHandler.class);
                for (Rune rune : bucketHandlerRunes) {
                    ((BucketHandler<?>) rune.getType().getActivator()).onBucketEmpty(plugin, bucketEmptyEvent, rune.getConfiguration());
                }
            }
            case PlayerBucketFillEvent bucketFillEvent -> {
                List<Rune> bucketHandlerRunes = runesByHandler.get(BucketHandler.class);
                for (Rune rune : bucketHandlerRunes) {
                    ((BucketHandler<?>) rune.getType().getActivator()).onBucketFill(plugin, bucketFillEvent, rune.getConfiguration());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + event);
        }
    }

}
