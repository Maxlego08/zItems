package fr.maxlego08.items.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
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

    private Optional<List<Rune>> getRunes(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) return Optional.empty();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if (!persistentDataContainer.has(this.runeManager.getKey(), PersistentDataType.LIST.listTypeFrom(this.runeManager.getDataType()))) {
            return Optional.empty();
        }

        var runes = persistentDataContainer.getOrDefault(this.runeManager.getKey(), PersistentDataType.LIST.listTypeFrom(this.runeManager.getDataType()), new ArrayList<>());
        runes = new ArrayList<>(runes);
        return Optional.of(runes);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled() || event instanceof CustomBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = getRunes(itemStack);
        if (optional.isEmpty()) return;

        var runes = optional.get();

        runes.removeIf(rune -> !rune.getConfiguration().contains(event.getBlock().getType()));
        if(runes.isEmpty()) return;

        Map<Location, List<ItemStack>> drops = new HashMap<>();
        RunePipeline pipeline = new RunePipeline(runes);
        Set<Block> blocks = pipeline.breakBlocks(plugin, event, drops);
        if (blocks.isEmpty()) return;

        event.setDropItems(false);

        for (Block block : blocks) {
            block.setType(Material.AIR);
        }
        drops.forEach((location, itemStacks) -> itemStacks.forEach(itemStack1 -> location.getWorld().dropItemNaturally(location, itemStack1)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;
        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = getRunes(itemStack);
        if (optional.isEmpty()) return;

        RunePipeline pipeline = new RunePipeline(optional.get());
        pipeline.interactBlock(plugin, event);
    }
}
