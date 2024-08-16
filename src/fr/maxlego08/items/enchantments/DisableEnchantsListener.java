package fr.maxlego08.items.enchantments;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.configurations.meta.ItemEnchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class DisableEnchantsListener implements Listener {

    private final ItemManager itemManager;

    public DisableEnchantsListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack item = anvilInventory.getFirstItem();
        ItemStack item2 = anvilInventory.getSecondItem();

        if(item == null || item2 == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;
        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;

        Item customItem = itemOptional.get();
        List<ItemEnchantment> disableEnchants = customItem.getConfiguration().getDisableEnchantments();
        if (disableEnchants.isEmpty()) return;

        for (ItemEnchantment enchantment : disableEnchants) {
            if(hasEnchant(item2, enchantment)) {
                event.setResult(null);
                return;
            }
        }
    }

    private boolean hasEnchant(ItemStack item, ItemEnchantment enchantment) {
        AtomicBoolean hasEnchant = new AtomicBoolean(false);
        if(!item.hasItemMeta()) {
            return false;
        }

        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            meta.getStoredEnchants().forEach((enchant, level) -> {
                if (enchant.equals(enchantment.enchantment())) {
                    if (enchantment.level() == -1) {
                        hasEnchant.set(true);
                        return;
                    }

                    if (level == enchantment.level()) {
                        hasEnchant.set(true);
                    }
                }
            });
        }
        if (hasEnchant.get()) {
            return true;
        }

        if (item.containsEnchantment(enchantment.enchantment())) {
            if (enchantment.level() == -1) {
                return true;
            }

            return item.getEnchantmentLevel(enchantment.enchantment()) == enchantment.level();
        }

        return false;
    }

}
