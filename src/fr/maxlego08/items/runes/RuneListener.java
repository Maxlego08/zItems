package fr.maxlego08.items.runes;

import com.jeff_media.armorequipevent.ArmorEquipEvent;
import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.runes.RunePipeline;
import fr.maxlego08.items.api.runes.handlers.InventorySlotChangeHandler;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class RuneListener implements Listener {

    private final ItemsPlugin plugin;
    private final RuneManager runeManager;

    public RuneListener(ItemsPlugin plugin, RuneManager runeManager) {
        this.plugin = plugin;
        this.runeManager = runeManager;
    }

    private void handleSlotChange(Player player, ItemStack oldItem, ItemStack newItem) {
        var oldOptional = this.runeManager.getRunes(oldItem);
        if (oldOptional.isPresent()) {
            RunePipeline pipeline = new RunePipeline(new ArrayList<>(oldOptional.get()));
            pipeline.pipeline(plugin, player, InventorySlotChangeHandler.InventorySlotChangeType.UNEQUIP);
        }

        var newOptional = this.runeManager.getRunes(newItem);
        if (newOptional.isPresent()) {
            RunePipeline pipeline = new RunePipeline(new ArrayList<>(newOptional.get()));
            pipeline.pipeline(plugin, player, InventorySlotChangeHandler.InventorySlotChangeType.EQUIP);
        }
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        if (event.isCancelled()) return;
        ItemStack oldItem = event.getOldArmorPiece();
        ItemStack newItem = event.getNewArmorPiece();
        handleSlotChange(event.getPlayer(), oldItem, newItem);
    }

    @EventHandler
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        ItemStack oldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());
        handleSlotChange(event.getPlayer(), oldItem, newItem);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        handleSlotChange(player, null, itemStack);
        itemStack = player.getInventory().getItemInOffHand();
        handleSlotChange(player, null, itemStack);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        handleSlotChange(player, itemStack, null);
        itemStack = player.getInventory().getItemInOffHand();
        handleSlotChange(player, itemStack, null);
    }

    @EventHandler
    public void onSlotChange(PlayerInventorySlotChangeEvent event) {
        ItemStack oldItem = event.getOldItemStack();
        ItemStack newItem = event.getNewItemStack();
        handleSlotChange(event.getPlayer(), oldItem, newItem);
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
        if (runes.isEmpty()) return;

        RunePipeline pipeline = new RunePipeline(runes);
        pipeline.pipeline(plugin, event);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        var entity = event.getEntity();
        var killer = entity.getKiller();
        if (killer == null) return;

        var itemStack = killer.getInventory().getItemInMainHand();
        var optional = this.runeManager.getRunes(itemStack);
        if (optional.isEmpty()) return;

        var runes = new ArrayList<>(optional.get());
        RunePipeline pipeline = new RunePipeline(runes);
        pipeline.pipeline(plugin, event);
    }

    // ToDo, rework for use hand and offhand
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        this.runeManager.onPlayerEvent(event);
    }
}
