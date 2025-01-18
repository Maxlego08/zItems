package fr.maxlego08.items.hook.shops;


import fr.maxlego08.items.api.shop.ShopProvider;
import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopGUIPlusProvider implements ShopProvider {
    @Override
    public boolean sellItems(OfflinePlayer player, ItemStack item, int amount, double multiplier) {
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
