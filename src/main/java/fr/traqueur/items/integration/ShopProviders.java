package fr.traqueur.items.integration;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.shop.ShopProvider;
import fr.traqueur.items.hooks.economyshopgui.EconomyShopGUIProvider;
import fr.traqueur.items.hooks.shopguiplus.ShopGUIPlusProvider;
import fr.traqueur.items.hooks.zshop.ZShopProvider;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum ShopProviders {

    Z_SHOP("ZShop", ZShopProvider::new, 1),
    ECONOMY_SHOP_GUI("EconomyShopGUI", EconomyShopGUIProvider::new, 0),
    SHOP_GUI_PLUS("ShopGUIPlus", ShopGUIPlusProvider::new, 0);

    public static ShopProviders FOUND_PROVIDER;

    private final String pluginName;
    private final Supplier<? extends ShopProvider> supplier;
    private final int priority;

    ShopProviders(String pluginName, Supplier<? extends ShopProvider> supplier, int priority) {
        this.pluginName = pluginName;
        this.supplier = supplier;
        this.priority = priority;
    }

    public static boolean initialize() {
        AtomicBoolean result = new AtomicBoolean(false);
        Stream.of(ShopProviders.values()).sorted(Comparator.comparingInt(sp -> sp.priority)).forEach(shopProvider -> {
            Logger.debug("Checking shop provider: " + shopProvider.pluginName());
            if (shopProvider.isEnable()) {
                ShopProvider.register(shopProvider.supplier.get());
                FOUND_PROVIDER = shopProvider;
                result.set(true);
            }
        });
        return result.get();
    }

    public String pluginName() {
        return pluginName;
    }

    private boolean isEnable() {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }


}
