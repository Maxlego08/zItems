package fr.maxlego08.items.hook;

import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;

public enum Hooks {

    JOBS(Plugins.JOBS, new JobsHook());

    private final Plugins plugin;
    private final Hook hook;

    Hooks(Plugins plugin, Hook hook) {
        this.plugin = plugin;
        this.hook = hook;
    }

    public Plugins getPlugin() {
        return plugin;
    }

    public Hook getHook() {
        return hook;
    }

}
