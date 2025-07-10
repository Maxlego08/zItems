package fr.maxlego08.items.api.shop;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public interface ShopProvider {

    boolean sellItems(OfflinePlayer player, ItemStack item, int amount, double multiplier);

}
