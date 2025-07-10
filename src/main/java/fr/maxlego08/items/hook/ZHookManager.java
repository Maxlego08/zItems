package fr.maxlego08.items.hook;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.items.api.hook.HookManager;
import fr.maxlego08.items.api.shop.ShopHook;
import fr.maxlego08.items.api.shop.ShopProvider;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ZHookManager implements HookManager {

    private final ItemPlugin plugin;
    private final Map<Plugins, Hook> hooks;

    private final Map<Plugins, ShopProvider> providers;

    public ZHookManager(ItemPlugin plugin) {
        this.plugin = plugin;
        this.hooks = new HashMap<>();
        this.providers = new HashMap<>();
    }

    @Override
    public void registerHook(Plugins plugin, Hook hook) {
        this.hooks.put(plugin, hook);
    }

    @Override
    public void loadHooks(Function<Plugins, Boolean> isEnable) {
        hooks.forEach((plugin, hook) -> {
            if (isEnable.apply(plugin)) {
                this.plugin.getLogger().info("Hook for plugin " + plugin.getName() + " is enabled");
                this.plugin.getServer().getPluginManager().registerEvents(hook, this.plugin);
                if(hook instanceof ShopHook shopHook) {
                    try {
                        ShopProvider provider = shopHook.getProvider().getConstructor().newInstance();
                        this.providers.put(plugin, provider);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Override
    public Map<Plugins, ShopProvider> getProviders() {
        return providers;
    }
}
