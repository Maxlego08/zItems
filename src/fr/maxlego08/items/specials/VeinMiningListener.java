package fr.maxlego08.items.specials;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.specials.VeinMiningConfiguration;
import fr.maxlego08.items.api.events.CustomBlockBreakEvent;
import fr.maxlego08.items.zcore.utils.VeinMiner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class VeinMiningListener extends SpecialHelper<VeinMiningConfiguration> implements Listener {

    public VeinMiningListener(ItemsPlugin plugin) {
        super(plugin, ItemType.VEIN_MINING);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.isCancelled() || event instanceof CustomBlockBreakEvent) return;

        var player = event.getPlayer();
        var itemStack = player.getInventory().getItemInMainHand();

        if (!isSpecialItem(itemStack)) return;

        var block = event.getBlock();

        var blocks = VeinMiner.getVeinBlocks(block, 2048);
        blocks.remove(block);
        
        blocks.removeIf(veinBlock -> !plugin.hasAccess(player, veinBlock.getLocation()));
        blocks.forEach(veinBlock -> veinBlock.breakNaturally(itemStack));
    }
}
