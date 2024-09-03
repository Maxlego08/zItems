package fr.maxlego08.items.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.runes.RunePipeline;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class RuneListener implements Listener {

    private final ItemsPlugin plugin;
    private final RuneManager runeManager;

    public RuneListener(ItemsPlugin plugin, RuneManager runeManager) {
        this.plugin = plugin;
        this.runeManager = runeManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event instanceof CustomBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = this.runeManager.getRunes(itemStack);
        if (optional.isEmpty()) return;

        var runes = new ArrayList<>(optional.get());
        runes.removeIf(rune -> !rune.getConfiguration().contains(event.getBlock().getType()));
        if(runes.isEmpty()) return;

        RunePipeline pipeline = new RunePipeline(this.runeManager, runes);
        pipeline.pipeline(plugin, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;
        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = this.runeManager.getRunes(itemStack);
        if (optional.isEmpty()) return;

        RunePipeline pipeline = new RunePipeline(this.runeManager, optional.get());
        pipeline.pipeline(plugin, event);
    }

    @EventHandler
    public void onJobsGainExp(JobsExpGainEventWrapper event) {
        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = this.runeManager.getRunes(itemStack);
        if (optional.isEmpty()) return;

        RunePipeline pipeline = new RunePipeline(this.runeManager, optional.get());
        pipeline.pipeline(plugin, event);
    }

    @EventHandler
    public void onJobsGainMoney(JobsPayementEventWrapper event) {
        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = this.runeManager.getRunes(itemStack);
        if (optional.isEmpty()) return;

        RunePipeline pipeline = new RunePipeline(this.runeManager, optional.get());
        pipeline.pipeline(plugin, event);
    }
}
