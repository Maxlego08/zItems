package fr.traqueur.items.effects.pipeline.entries.combat;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.annotations.AutoListener;
import fr.traqueur.items.effects.pipeline.entries.combat.PlayerDefendEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * There is no single "the item" a victim reacts with when taking damage — unlike an
 * attacker's weapon, gear that could react spans up to 6 slots (armor, main hand, off
 * hand). Rather than pick one arbitrarily, this dispatches once per equipped item,
 * wrapped in a synthetic {@link PlayerDefendEvent} (see that class for why it isn't
 * the raw {@code EntityDamageEvent} itself). Each dispatch only ever sees the effects
 * attached to *that* item, so this can't cross-fire between slots.
 */
@AutoListener
public class DefendTransitionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        var dispatcher = JavaPlugin.getPlugin(ItemsPlugin.class).getDispatcher();
        PlayerInventory inventory = player.getInventory();

        for (ItemStack item : equippedItems(inventory)) {
            if (item == null || item.getType().isAir()) {
                continue;
            }
            dispatcher.dispatch(player, item, new PlayerDefendEvent(player, event));
        }
    }

    private ItemStack[] equippedItems(PlayerInventory inventory) {
        ItemStack[] items = new ItemStack[inventory.getArmorContents().length + 2];
        System.arraycopy(inventory.getArmorContents(), 0, items, 0, inventory.getArmorContents().length);
        items[items.length - 2] = inventory.getItemInMainHand();
        items[items.length - 1] = inventory.getItemInOffHand();
        return items;
    }
}
