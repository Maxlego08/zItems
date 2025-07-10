package fr.maxlego08.items.listener;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class GrindstoneListener implements Listener {

    private final ItemManager itemManager;

    public GrindstoneListener(ItemManager itemManager) {
       this.itemManager = itemManager;
    }


    private Optional<Item> getCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return Optional.empty();

        return itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
    }

    @EventHandler
    public void onGrindstone(PrepareGrindstoneEvent event) {
        ItemStack upperItem = event.getInventory().getUpperItem();
        ItemStack lowerItem = event.getInventory().getLowerItem();


        var optionalUpperItem = this.getCustomItem(upperItem);
        var optionalLowerItem = this.getCustomItem(lowerItem);

        if (optionalUpperItem.isEmpty() && optionalLowerItem.isEmpty()) return;

        if (optionalUpperItem.isEmpty() || optionalLowerItem.isEmpty()) {
            event.setResult(null);
            return;
        }

        Item upper = optionalUpperItem.get();
        Item lower = optionalLowerItem.get();

        if(!upper.getName().equals(lower.getName())
                || !upper.getConfiguration().isGrindstoneEnabled()
                || !lower.getConfiguration().isGrindstoneEnabled()) {
            event.setResult(null);
        }
    }
}
