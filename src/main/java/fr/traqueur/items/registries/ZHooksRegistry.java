package fr.traqueur.items.registries;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.VersionFilter;
import fr.traqueur.items.api.annotations.AutoHook;
import fr.traqueur.items.api.hooks.Hook;
import fr.traqueur.items.api.registries.HooksRegistry;
import fr.traqueur.items.utils.ReflectionsCache;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public class ZHooksRegistry implements HooksRegistry {

    private final Map<String, Hook> hooks;
    private final Set<String> scannedPackages;
    private final Set<String> enabledHooks;

    public ZHooksRegistry() {
        this.hooks = new HashMap<>();
        this.scannedPackages = new HashSet<>();
        this.enabledHooks = new HashSet<>();
    }

    @Override
    public void register(String s, Hook item) {
        this.hooks.put(s, item);
    }

    @Override
    public Hook getById(String s) {
        return this.hooks.get(s);
    }

    @Override
    public Collection<Hook> getAll() {
        return this.hooks.values();
    }

    @Override
    public void clear() {
        this.hooks.clear();
        this.scannedPackages.clear();
        Logger.debug("Cleared all registered hooks.");
    }

    @Override
    public void loadAll() {
        for (Map.Entry<String, Hook> entry : this.hooks.entrySet()) {
            String hookName = entry.getKey();
            if (Bukkit.getPluginManager().getPlugin(hookName) == null) {
                continue;
            }
            entry.getValue().onLoad();
            Logger.debug("Called onLoad for hook: {}", hookName);
        }
    }

    @Override
    public void enableAll() {
        for (Map.Entry<String, Hook> stringHookEntry : this.hooks.entrySet()) {
            String hookName = stringHookEntry.getKey();
            Hook hook = stringHookEntry.getValue();
            if (Bukkit.getPluginManager().getPlugin(hookName) == null) {
                Logger.debug("Hook " + hookName + " not found, skipping...");
                continue;
            }
            hook.onEnable();
            enabledHooks.add(hookName);
            Logger.debug("Enabled hook: " + hookName);
        }
    }

    @Override
    public void disableAll() {
        for (String hookName : enabledHooks) {
            Hook hook = this.hooks.get(hookName);
            if (hook != null) {
                hook.onDisable();
                Logger.debug("Called onDisable for hook: {}", hookName);
            }
        }
        enabledHooks.clear();
    }

    @Override
    public void scanPackage(JavaPlugin plugin, String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            Logger.warning("Cannot scan null or empty package name for hooks.");
            return;
        }

        if (scannedPackages.contains(packageName)) {
            Logger.debug("Package {} already scanned for hooks, skipping.", packageName);
            return;
        }

        Logger.info("Scanning package <aqua>{}<reset> for Hooks...", packageName);

        try {
            Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, packageName);
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoHook.class);

            int count = 0;
            for (Class<?> clazz : annotatedClasses) {
                if (!Hook.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @AutoHook but does not implement Hook. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }

                // Check if it's abstract or interface
                if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
                    Logger.warning("Class <yellow>{}<reset> is abstract or interface, cannot instantiate. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }

                if (!VersionFilter.passes(clazz, clazz.getSimpleName())) {
                    continue;
                }

                try {
                    AutoHook annotation = clazz.getAnnotation(AutoHook.class);
                    String pluginName = annotation.value();

                    if (pluginName == null || pluginName.trim().isEmpty()) {
                        Logger.warning("Hook <yellow>{}<reset> has empty plugin name. Skipping.",
                                clazz.getSimpleName());
                        continue;
                    }

                    Hook hook = instantiateHook(clazz, plugin);
                    if (hook != null) {
                        this.register(pluginName, hook);
                        count++;
                        Logger.debug("Registered hook: <aqua>{}<reset> -> {}",
                                pluginName, clazz.getSimpleName());
                    }
                } catch (LinkageError e) {
                    Logger.warning("Skipping hook <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
                } catch (Exception e) {
                    Logger.severe("Failed to instantiate hook {}: {}",
                            e, clazz.getSimpleName(), e.getMessage());
                }
            }

            scannedPackages.add(packageName);
            Logger.info("Registered <gold>{}<reset> hook(s) from package {}.", count, packageName);

        } catch (Exception e) {
            Logger.severe("Failed to scan package {} for hooks: {}", e, packageName, e.getMessage());
        }
    }

    /**
     * Attempts to instantiate a Hook class.
     * Tries constructors in this order:
     * 1. Constructor with JavaPlugin parameter
     * 2. No-args constructor
     *
     * @param clazz the Hook class to instantiate
     * @param plugin the plugin instance
     * @return the instantiated Hook, or null if instantiation failed
     */
    private Hook instantiateHook(Class<?> clazz, JavaPlugin plugin) {
        try {
            // Try constructor with JavaPlugin parameter
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor(JavaPlugin.class);
                constructor.setAccessible(true);
                return (Hook) constructor.newInstance(plugin);
            } catch (NoSuchMethodException e) {
                // Fall through to no-args constructor
            }

            // Try no-args constructor
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (Hook) constructor.newInstance();

        } catch (Exception e) {
            Logger.severe("Failed to instantiate hook {}: {}",
                    e, clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }
}
