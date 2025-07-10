package fr.maxlego08.items.hooks;

import dev.lone.itemsadder.api.CustomBlock;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.global.Strips;
import fr.maxlego08.items.api.hook.Hook;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemsAdderHook implements Hook {

    private final ItemPlugin plugin;

    public ItemsAdderHook(ItemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {

        if (event.useInteractedBlock() == Event.Result.DENY) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var block = event.getClickedBlock();
        if (block == null) return;

        if (block.getType() != Material.NOTE_BLOCK) return;

        var itemStack = event.getItem();
        if (itemStack == null) return;

        var config = plugin.getGlobalConfiguration().getStripLogConfiguration();
        if (!config.enable()) return;

        if (config.tags().stream().noneMatch(tag -> tag.isTagged(itemStack.getType()))) return;

        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) return;

        for (Strips strip : config.strips()) {
            if (!customBlock.getId().equals(strip.from())) return;

            CustomBlock.place(strip.to(), block.getLocation());
            if (config.damage() > 0) {
                itemStack.damage(config.damage(), event.getPlayer());
            }
            break;
        }
    }
}
