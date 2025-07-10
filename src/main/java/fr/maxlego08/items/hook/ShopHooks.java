package fr.maxlego08.items.hook;

import fr.maxlego08.items.api.shop.ShopHook;
import fr.maxlego08.items.api.shop.ShopProvider;
import fr.maxlego08.items.hooks.EconomyShopGUIProvider;
import fr.maxlego08.items.hooks.ShopGUIPlusProvider;
import fr.maxlego08.items.hooks.ZShopProvider;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;

public enum ShopHooks implements ShopHook {

    SHOPGUIPLUS(Plugins.SHOPGUIPLUS, ShopGUIPlusProvider.class),
    ZSHOP(Plugins.ZSHOP, ZShopProvider.class),
    ECONOMYSHOPGUI(Plugins.ECONOMYSHPOGUI, EconomyShopGUIProvider.class);

    private final Plugins plugin;
    private final Class<? extends ShopProvider> clazz;

    ShopHooks(Plugins plugin, Class<? extends ShopProvider> clazz) {
        this.plugin = plugin;
        this.clazz = clazz;
    }

    @Override
    public Class<? extends ShopProvider> getProvider() {
        return this.clazz;
    }

    @Override
    public Plugins getPlugin() {
        return this.plugin;
    }
}
