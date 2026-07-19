package fr.traqueur.items.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoListener;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.effects.events.PlayerArmorUnequipEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * {@link PlayerArmorChangeEvent} only ever lets the automatic dispatch (via
 * {@link fr.traqueur.items.effects.extractors.PlayerArmorChangeExtractor}) resolve the
 * armor piece being put on — a dispatch can't resolve two items from one event. This
 * listener fires a second, manual dispatch for the piece that just came off, wrapped
 * in a synthetic {@link PlayerArmorUnequipEvent} so {@code ARMOR_UNEQUIP} entries can
 * gate on it. Does not interfere with the existing automatic {@code ARMOR_EQUIP} path.
 */
@AutoListener
@PaperOnly
public class ArmorChangeTransitionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArmorChange(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack oldItem = event.getOldItem();
        if (oldItem == null || oldItem.getType().isAir()) {
            return;
        }

        JavaPlugin.getPlugin(ItemsPlugin.class).getDispatcher()
                .dispatch(player, oldItem, new PlayerArmorUnequipEvent(player));
    }
}
