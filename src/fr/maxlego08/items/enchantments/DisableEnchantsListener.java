package fr.maxlego08.items.enchantments;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.configurations.meta.ItemEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

public class DisableEnchantsListener implements Listener {

    private final ItemManager itemManager;

    public DisableEnchantsListener(ItemManager itemManager) {
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

    private boolean shouldDisableEnchantment(ItemStack item, ItemEnchantment enchantment) {
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();


        if (meta instanceof EnchantmentStorageMeta enchantMeta) {
            return enchantMeta.getStoredEnchants().entrySet().stream()
                    .anyMatch(entry -> matchesEnchantment(entry.getKey(), entry.getValue(), enchantment));
        }

        return item.getEnchantments().entrySet().stream()
                .anyMatch(entry -> matchesEnchantment(entry.getKey(), entry.getValue(), enchantment));
    }

    private boolean matchesEnchantment(Enchantment enchantment, int level, ItemEnchantment disableEnchantment) {
        return (disableEnchantment.enchantment() == null || enchantment.equals(disableEnchantment.enchantment()))
                && (disableEnchantment.level() == -1 || level == disableEnchantment.level());
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        ItemStack item = anvilInventory.getFirstItem();
        ItemStack item2 = anvilInventory.getSecondItem();

        if (item == null || item2 == null) return;

        getCustomItem(item).ifPresent(customItem -> {
            List<ItemEnchantment> disableEnchants = customItem.getConfiguration().getDisableEnchantments();
            if (disableEnchants.isEmpty()) return;
            System.out.println("Anvil");
            for (ItemEnchantment enchantment : disableEnchants) {
                if (shouldDisableEnchantment(item2, enchantment)) {
                    event.setResult(null);
                    return;
                }
            }
        });
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();

        getCustomItem(item).ifPresent(customItem -> {
            List<ItemEnchantment> disableEnchants = customItem.getConfiguration().getDisableEnchantments();
            if (disableEnchants.isEmpty()) return;

            disableEnchants.forEach(enchantment -> {
                if (enchantment.enchantment() == null) {
                    if (enchantment.level() == -1) {
                        event.getEnchantsToAdd().clear();
                    } else {
                        event.getEnchantsToAdd().entrySet().removeIf(entry -> entry.getValue() == enchantment.level());
                    }
                } else {
                    if (enchantment.level() == -1) {
                        event.getEnchantsToAdd().remove(enchantment.enchantment());
                    } else {
                        event.getEnchantsToAdd().entrySet().removeIf(entry -> entry.getKey().equals(enchantment.enchantment()) && entry.getValue() == enchantment.level());
                    }
                }
            });
        });
    }

    @EventHandler
    public void onPrepareEnchantTable(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();

        getCustomItem(item).ifPresent(customItem -> {
            List<ItemEnchantment> disableEnchants = customItem.getConfiguration().getDisableEnchantments();
            if (disableEnchants.isEmpty()) return;

            EnchantmentOffer[] offers = event.getOffers();
            for (int i = 0; i < offers.length; i++) {
                EnchantmentOffer offer = offers[i];
                if (offer != null) {
                    for (ItemEnchantment enchantment : disableEnchants) {
                        if (matchesEnchantment(offer.getEnchantment(), offer.getEnchantmentLevel(), enchantment)) {
                            event.getOffers()[i] = null;
                            break;
                        }
                    }
                }
            }
        });
    }

}
