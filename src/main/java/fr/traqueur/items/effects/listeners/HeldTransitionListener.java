package fr.traqueur.items.effects.listeners;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoListener;
import fr.traqueur.items.effects.events.PlayerItemUnheldEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * {@link PlayerItemHeldEvent} only ever lets the automatic dispatch (via
 * {@link fr.traqueur.items.effects.extractors.PlayerItemHeldExtractor}) resolve the
 * item about to become held — a dispatch can't resolve two items from one event. This
 * listener fires a second, manual dispatch for the item that just stopped being held,
 * wrapped in a synthetic {@link PlayerItemUnheldEvent} so {@code UNHELD} entries can
 * gate on it. Does not interfere with the existing automatic {@code HELD} path.
 */
@AutoListener
public class HeldTransitionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        if (oldItem == null || oldItem.getType().isAir()) {
            return;
        }

        JavaPlugin.getPlugin(ItemsPlugin.class).getDispatcher()
                .dispatch(player, oldItem, new PlayerItemUnheldEvent(player));
    }
}
