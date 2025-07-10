package fr.maxlego08.items.api.shop;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public interface ShopProvider {

    boolean sellItems(ItemPlugin plugin, ItemStack item, int amount, double multiplier, OfflinePlayer player);

}
