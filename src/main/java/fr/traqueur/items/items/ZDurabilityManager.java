package fr.traqueur.items.items;

import fr.traqueur.items.api.items.DurabilityMode;
import fr.traqueur.items.api.managers.DurabilityManager;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.items.serialization.Keys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class ZDurabilityManager implements DurabilityManager {

    @Override
    public void applyDamage(ItemStack item, int damage, Player player) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String modeStr = Keys.DURABILITY_MODE
                .get(meta.getPersistentDataContainer())
                .orElse(DurabilityMode.VANILLA.name());

        if (DurabilityMode.CUSTOM.name().equals(modeStr)) {
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(player, item, damage);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            int current = Keys.CUSTOM_DURABILITY.get(pdc, 0);
            int newVal = current - event.getDamage();
            if (newVal <= 0) {
                player.getInventory().setItemInMainHand(null);
            } else {
                Keys.CUSTOM_DURABILITY.set(pdc, newVal);
                item.setItemMeta(meta);
                // Force a SET_SLOT packet so the DurabilityPacketListener refreshes the lore
                player.getInventory().setItemInMainHand(item);
            }
        } else {
            ItemUtil.applyDamageToItem(item, damage, player);
        }
    }
}
