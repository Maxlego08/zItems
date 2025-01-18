package fr.maxlego08.items.hook.shops;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.shop.ShopProvider;
import fr.maxlego08.zshop.api.ShopManager;
import fr.maxlego08.zshop.api.buttons.ItemButton;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ZShopProvider implements ShopProvider {

    @Override
    public boolean sellItems(OfflinePlayer player, ItemStack item, int amount, double multiplier) {
        if(!player.isOnline()) {
            return false;
        }
        var register = JavaPlugin.getPlugin(ItemsPlugin.class).getServer()
                .getServicesManager().getRegistration(ShopManager.class);
        if (register == null) {
            return false;
        }
        var shopManager = register.getProvider();
        for (ItemButton itemButton : shopManager.getItemButtons()) {
            if(!itemButton.canSell()) {
                continue;
            }
            if(itemButton.getItemStack().build(player.getPlayer(), false).isSimilar(item)) {
                double price = itemButton.getSellPrice(player.getPlayer(), amount);
                itemButton.getEconomy().depositMoney(player, price*multiplier, "Automatic sell");
                return true;
            }
        }
        return false;
    }
}
