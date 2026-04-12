package fr.traqueur.items.registries;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.VersionFilter;
import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.utils.ReflectionsCache;
import fr.traqueur.structura.registries.PolymorphicRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of HandlersRegistry that discovers and registers EffectHandlers.
 * <p>
 * This registry:
 * <ul>
 *   <li>Scans packages for @EffectMeta annotated classes</li>
 *   <li>Instantiates handlers via reflection (supports constructors with/without JavaPlugin)</li>
 *   <li>Registers handlers by their effect ID</li>
 *   <li>Manages polymorphic EffectSettings registration via Structura</li>
 * </ul>
 */
public class ZHandlersRegistry implements HandlersRegistry {

    private final ItemsPlugin plugin;
    private final Map<String, EffectHandler<?>> handlers;
    private final Set<String> scannedPackages;

    public ZHandlersRegistry(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.handlers = new HashMap<>();
        this.scannedPackages = new HashSet<>();

        // Create the polymorphic registry for EffectSettings
        PolymorphicRegistry.create(EffectSettings.class, registry -> {
            // Empty at initialization, will be filled after scanning
        });
    }

    @Override
    public void scanPackage(JavaPlugin plugin, String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            Logger.warning("Cannot scan null or empty package name.");
            return;
        }

        if (scannedPackages.contains(packageName)) {
            Logger.debug("Package {} already scanned, skipping.", packageName);
            return;
        }

        Logger.info("Scanning package <aqua>{}<reset> for EffectHandlers...", packageName);

        try {
            Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, packageName);

            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoEffect.class);

            int count = 0;
            for (Class<?> clazz : annotatedClasses) {
                if (!EffectHandler.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @EffectMeta but does not implement EffectHandler. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                //noinspection unchecked
                if (registerEffectHandler((Class<? extends EffectHandler<?>>) clazz)) {
                    count++;
                }
            }

            scannedPackages.add(packageName);
            Logger.info("Registered <gold>{}<reset> effect handler(s) from package {}.", count, packageName);

        } catch (Exception e) {
            Logger.severe("Failed to scan package {}: {}", e, packageName);
        }
    }

    @Override
    public Set<String> getScannedPackages() {
        return Collections.unmodifiableSet(scannedPackages);
    }

    @Override
    public void register(String effectId, EffectHandler<?> handler) {
        if (effectId == null || effectId.trim().isEmpty()) {
            Logger.severe("Cannot register handler with null or empty effect ID.");
            return;
        }

        if (handler == null) {
            Logger.severe("Cannot register null handler for effect ID: {}", effectId);
            return;
        }

        if (this.handlers.containsKey(effectId)) {
            Logger.warning("Effect ID <yellow>{}<reset> is already registered. Overwriting with {}.",
                    effectId, handler.getClass().getSimpleName());
        }

        this.handlers.put(effectId, handler);
        Logger.debug("Registered effect handler: <aqua>{}<reset> -> {}",
                effectId, handler.getClass().getSimpleName());

        // Register the EffectSettings in the polymorphic registry
        registerHandlerSettings(effectId, handler);
    }

    @Override
    public EffectHandler<?> getById(String effectId) {
        return this.handlers.get(effectId);
    }

    @Override
    public Collection<EffectHandler<?>> getAll() {
        return this.handlers.values();
    }

    @Override
    public void clear() {
        this.handlers.clear();
        Logger.info("Cleared all registered effect handlers.");
    }

    /**
     * Registers a single effect handler class.
     * Tries to instantiate using a constructor with ItemsPlugin parameter first,
     * then falls back to a no-args constructor if not available.
     *
     * @return true if successfully registered, false otherwise
     */
    private boolean registerEffectHandler(Class<? extends EffectHandler<?>> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            Logger.debug("Class {} is abstract or an interface. Skipping.", clazz.getSimpleName());
            return false;
        }

        if (!VersionFilter.passes(clazz, clazz.getSimpleName())) {
            return false;
        }

        try {
            AutoEffect meta = clazz.getAnnotation(AutoEffect.class);
            String effectId = meta.value();

            if (this.handlers.containsKey(effectId)) {
                Logger.warning("Effect ID <yellow>{}<reset> is already registered. Skipping class {}.",
                        effectId, clazz.getSimpleName());
                return false;
            }

            EffectHandler<?> handler = instantiateHandler(clazz);

            this.handlers.put(effectId, handler);
            Logger.debug("Registered effect handler: <aqua>{}<reset> -> {}", effectId, clazz.getSimpleName());

            // Register the EffectSettings in the polymorphic registry
            registerHandlerSettings(effectId, handler);

            return true;

        } catch (LinkageError e) {
            Logger.warning("Skipping EffectHandler <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
            return false;
        } catch (Exception e) {
            Logger.severe("Failed to instantiate effect handler: {}", e, clazz.getName());
            return false;
        }
    }

    /**
     * Instantiates an effect handler using the appropriate constructor.
     * Tries constructor with ItemsPlugin parameter first, then no-args constructor.
     *
     * @param clazz the effect handler class to instantiate
     * @return the instantiated handler
     * @throws Exception if instantiation fails
     */
    private EffectHandler<?> instantiateHandler(Class<? extends EffectHandler<?>> clazz) throws Exception {
        // Try constructor with ItemsPlugin parameter first
        try {
            return clazz.getDeclaredConstructor(ItemsPlugin.class).newInstance(this.plugin);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(
                        "Effect handler " + clazz.getSimpleName() +
                                " must have either a no-args constructor or a constructor with ItemsPlugin parameter."
                );
            }
        }
    }

    /**
     * Registers the settings class of a handler in the PolymorphicRegistry.
     */
    private void registerHandlerSettings(String effectId, EffectHandler<?> handler) {
        Class<? extends EffectSettings> settingsClass = handler.settingsType();
        if (settingsClass != null) {
            try {
                PolymorphicRegistry<EffectSettings> registry = PolymorphicRegistry.get(EffectSettings.class);
                if (registry.get(effectId).isPresent()) {
                    Logger.debug("EffectSettings class {} already registered.", settingsClass.getSimpleName());
                    return;
                }

                registry.register(effectId, settingsClass);
                Logger.debug("Registered EffectSettings class: <aqua>{}<reset>", settingsClass.getSimpleName());
            } catch (Exception e) {
                Logger.severe("Failed to register EffectSettings class: {}", e, settingsClass.getName());
            }
        }
    }
}