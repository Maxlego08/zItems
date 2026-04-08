package fr.traqueur.items.listeners;

import fr.traqueur.items.api.blocks.CustomBlockProvider;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.managers.DurabilityManager;
import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.settings.Settings;
import fr.traqueur.items.settings.PluginSettings;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class StripLogListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack itemStack = event.getItem();
        if (itemStack == null) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        PluginSettings settings = Settings.get(PluginSettings.class);
        if (!settings.stripLogs().matchesTool(itemStack)) {
            return;
        }

        CustomBlockProviderRegistry registry = Registry.get(CustomBlockProviderRegistry.class);
        for (PluginSettings.StripLog strip : settings.stripLogs().strips()) {
            for (CustomBlockProvider customBlockProvider : registry.getAll()) {
                customBlockProvider.getCustomBlockId(block).ifPresent(blockId -> {
                    if (!blockId.equals(strip.from())) return;
                    customBlockProvider.placeCustomBlock(strip.to(), block);
                    if (settings.stripLogs().damage() > 0) {
                        JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(itemStack, settings.stripLogs().damage(), event.getPlayer());
                    }
                });
            }
            Material material = Material.matchMaterial(strip.from());
            if (material != null && block.getType() == material) {
                Material toMaterial = Material.matchMaterial(strip.to());
                if (toMaterial != null) {
                    block.setType(toMaterial);
                    if (settings.stripLogs().damage() > 0) {
                        JavaPlugin.getPlugin(ItemsPlugin.class).getManager(DurabilityManager.class).applyDamage(itemStack, settings.stripLogs().damage(), event.getPlayer());
                    }
                }
            }
        }
    }
}
