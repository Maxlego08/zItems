package fr.maxlego08.items.hook.shops;

import fr.maxlego08.items.api.shop.ShopProvider;
import me.gypopo.economyshopgui.api.EconomyShopGUIHook;
import me.gypopo.economyshopgui.api.objects.SellPrice;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class EconomyShopGUIProvider implements ShopProvider {
    @Override
    public boolean sellItems(OfflinePlayer player, ItemStack item, int amount, double multiplier) {
        Optional<SellPrice> optional = EconomyShopGUIHook.getSellPrice(player, item);
        if (optional.isEmpty())
            return false;
        SellPrice price = optional.get();
        EconomyShopGUIHook.getEcon(price.getShopItem().getEcoType()).depositBalance(player, price.getPrice(price.getShopItem().getEcoType()) * amount * multiplier);
        return true;
    }
}
