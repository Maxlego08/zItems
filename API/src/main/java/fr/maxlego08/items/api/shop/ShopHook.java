package fr.maxlego08.items.api.shop;

import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.items.api.utils.Plugins;

public interface ShopHook extends Hook {

    Class<? extends ShopProvider> getProvider();

    Plugins getPlugin();
}