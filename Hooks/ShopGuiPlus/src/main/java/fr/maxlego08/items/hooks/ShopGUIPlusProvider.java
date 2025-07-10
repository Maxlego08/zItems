package fr.maxlego08.items.hooks;


import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.shop.ShopProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusProvider implements ShopProvider {
    @Override
    public boolean sellItems(ItemPlugin plugin, ItemStack item, int amount, double multiplier, OfflinePlayer player) {
        if(!player.isOnline()) {
            return false;
        }
        double price = ShopGuiPlusApi.getItemStackPriceSell(player.getPlayer(), item);
        if (price == -1) {
            return false;
        }

        double total = price * amount * multiplier;
        ShopGuiPlusApi.getItemStackShop(item).getEconomyProvider().deposit(player.getPlayer(), total);
        return true;
    }
}
