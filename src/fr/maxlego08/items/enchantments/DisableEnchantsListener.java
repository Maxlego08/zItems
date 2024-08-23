package fr.maxlego08.items.enchantments;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.configurations.meta.ItemEnchantment;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

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
            Predicate<ItemStack> hasEnchantPredicate = (itemStack) -> {
                AtomicBoolean hasEnchant = new AtomicBoolean(false);
                if(!item.hasItemMeta()) {
                    return false;
                }

                if (item.getItemMeta() instanceof EnchantmentStorageMeta metaEnchant) {
                    metaEnchant.getStoredEnchants().forEach((enchant, level) -> {
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
            };
            if(hasEnchantPredicate.test(item2)) {
                event.setResult(null);
                return;
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();

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
            if (event.getEnchantsToAdd().containsKey(enchantment.enchantment())) {
                if (enchantment.level() == -1) {
                    event.getEnchantsToAdd().remove(enchantment.enchantment());
                    continue;
                }

                if (event.getEnchantsToAdd().get(enchantment.enchantment()) == enchantment.level()) {
                    event.getEnchantsToAdd().remove(enchantment.enchantment());
                }
            }
        }
    }

    @EventHandler
    public void onPrepareEnchantTable(PrepareItemEnchantEvent event) {
        ItemStack item = event.getItem();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;
        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;

        Item customItem = itemOptional.get();
        List<ItemEnchantment> disableEnchants = customItem.getConfiguration().getDisableEnchantments();
        if (disableEnchants.isEmpty()) return;

        EnchantmentOffer[] offers = Arrays.copyOf(event.getOffers(), event.getOffers().length);
        List<EnchantmentOffer> newOffers = Arrays.asList(offers);

        for (ItemEnchantment enchantment : disableEnchants) {

            Predicate<EnchantmentOffer> hasEnchant = (offer) -> offer.getEnchantment().equals(enchantment.enchantment())
                    && (enchantment.level() == -1 || offer.getEnchantmentLevel() == enchantment.level());

            newOffers.stream().filter(hasEnchant).forEach(offer -> {
                int index = newOffers.indexOf(offer);
                event.getOffers()[index] = null;
            });
        }
    }

}
