package fr.maxlego08.items.api.hook;

import fr.maxlego08.items.zcore.utils.plugins.Plugins;

import java.util.function.Consumer;
import java.util.function.Function;

public interface HookManager {

    void registerHook(Consumer<String> log, Plugins plugin, Hook hook);

    void loadHooks(Function<Plugins, Boolean> isEnable);
}
