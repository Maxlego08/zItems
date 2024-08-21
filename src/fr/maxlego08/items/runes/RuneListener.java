package fr.maxlego08.items.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.maxlego08.items.api.runes.RuneType;
import fr.maxlego08.items.api.runes.configurations.RuneVeinMiningConfiguration;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        if (!persistentDataContainer.has(this.runeManager.getKey(), this.runeManager.getDataType())) {
            return Optional.empty();
        }

        var runes = persistentDataContainer.getOrDefault(this.runeManager.getKey(), this.runeManager.getDataType(), new ArrayList<>());
        return Optional.of(runes);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled() || event instanceof CustomBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = getRunes(itemStack);
        if (optional.isEmpty()) return;


        var optionalRuneFarming = optional.get().stream().filter(rune -> rune.getType() == RuneType.FARMING_HOE).findFirst();

        var optionalRuneMelt = optional.get().stream().filter(rune -> rune.getType() == RuneType.MELT_MINING).findFirst();

        var optionalRuneVein = optional.get().stream().filter(rune -> rune.getType() == RuneType.VEIN_MINING).findFirst();

        if (optionalRuneFarming.isPresent()) {
            FarmingHoe.breakBlocks(plugin, event, optionalRuneFarming.get().getConfiguration());
            return;
        }

        if (optionalRuneVein.isEmpty()) {
            if (optionalRuneMelt.isPresent()) {
                MeltMining.meltBlocks(event, event.getBlock(), itemStack);
            }
            return;
        }

        Rune rune = optionalRuneVein.get();
        RuneVeinMiningConfiguration configuration = rune.getConfiguration();

        var block = event.getBlock();
        if (!configuration.contains(block.getType())) return;

        var blocks = VeinMiner.getVeinBlocks(block, configuration.blockLimit());

        blocks.removeIf(veinBlock -> !plugin.hasAccess(player, veinBlock.getLocation()) || !configuration.contains(veinBlock.getType()));
        if (optionalRuneMelt.isPresent()) {
            MeltMining.meltBlocks(event, blocks, itemStack);
        } else {
            blocks.remove(block);
            blocks.forEach(veinBlock -> veinBlock.breakNaturally(itemStack));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getHand() != EquipmentSlot.HAND) return;
        if (event.useInteractedBlock() == Event.Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();
        var optional = getRunes(itemStack);
        if (optional.isEmpty()) return;

        var optionalRuneFarming = optional.get().stream().filter(rune -> rune.getType() == RuneType.FARMING_HOE).findFirst();
        if (optionalRuneFarming.isEmpty()) return;

        FarmingHoe.interact(event, itemStack, optionalRuneFarming.get().getConfiguration());
    }
}
