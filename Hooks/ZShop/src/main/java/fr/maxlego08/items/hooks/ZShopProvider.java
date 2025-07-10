package fr.maxlego08.items.hooks;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.shop.ShopProvider;
import fr.maxlego08.shop.api.ShopManager;
import fr.maxlego08.shop.api.buttons.ItemButton;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ZShopProvider implements ShopProvider {

    @Override
    public boolean sellItems(ItemPlugin plugin, ItemStack item, int amount, double multiplier, OfflinePlayer player) {
        if (!player.isOnline()) {
            return false;
        }

        var register = JavaPlugin.getPlugin(ItemPlugin.class).getServer().getServicesManager().getRegistration(ShopManager.class);
        if (register == null) {
            return false;
        }
        var shopManager = register.getProvider();
        for (ItemButton itemButton : shopManager.getItemButtons()) {
            if (!itemButton.canSell()) {
                continue;
            }
            if (itemButton.getItemStack().build(player.getPlayer(), false).isSimilar(item)) {
                double price = itemButton.getSellPrice(player.getPlayer(), amount);
                itemButton.getEconomy().depositMoney(player, price * multiplier, "Automatic sell");
                return true;
            }
        }
        return false;
    }
}
