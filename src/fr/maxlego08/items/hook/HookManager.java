package fr.maxlego08.items.hook;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.Hook;

public class HookManager {

    private final ItemPlugin plugin;

    public HookManager(ItemPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerHook(Hook hook) {
        this.plugin.getLogger().info("Hooking into " + hook.getName());
        this.plugin.getServer().getPluginManager().registerEvents(hook, this.plugin);
    }
}
