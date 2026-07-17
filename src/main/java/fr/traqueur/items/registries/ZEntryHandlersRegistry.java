package fr.traqueur.items.registries;

import fr.traqueur.items.VersionFilter;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.items.api.registries.EntryHandlersRegistry;
import fr.traqueur.items.utils.ReflectionsCache;
import fr.traqueur.structura.registries.PolymorphicRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of EntryHandlersRegistry that discovers and registers EntryHandlers.
 * <p>
 * Mirrors {@link ZHandlersRegistry}, but for {@link EntryHandler}s.
 */
public class ZEntryHandlersRegistry implements EntryHandlersRegistry {

    private final ItemsPlugin plugin;
    private final Map<String, EntryHandler<?>> handlers;
    private final Set<String> scannedPackages;

    public ZEntryHandlersRegistry(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.handlers = new HashMap<>();
        this.scannedPackages = new HashSet<>();

        // Create the polymorphic registry for EntrySettings
        PolymorphicRegistry.create(EntrySettings.class, registry -> {
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

        Logger.info("Scanning package <aqua>{}<reset> for EntryHandlers...", packageName);

        try {
            Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, packageName);

            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoEntry.class);

            int count = 0;
            for (Class<?> clazz : annotatedClasses) {
                if (!EntryHandler.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @AutoEntry but does not implement EntryHandler. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                //noinspection unchecked
                if (registerEntryHandler((Class<? extends EntryHandler<?>>) clazz)) {
                    count++;
                }
            }

            scannedPackages.add(packageName);
            Logger.info("Registered <gold>{}<reset> entry handler(s) from package {}.", count, packageName);

        } catch (Exception e) {
            Logger.severe("Failed to scan package {}: {}", e, packageName);
        }
    }

    @Override
    public Set<String> getScannedPackages() {
        return Collections.unmodifiableSet(scannedPackages);
    }

    @Override
    public void register(String entryId, EntryHandler<?> handler) {
        if (entryId == null || entryId.trim().isEmpty()) {
            Logger.severe("Cannot register handler with null or empty entry ID.");
            return;
        }

        if (handler == null) {
            Logger.severe("Cannot register null handler for entry ID: {}", entryId);
            return;
        }

        if (this.handlers.containsKey(entryId)) {
            Logger.warning("Entry ID <yellow>{}<reset> is already registered. Overwriting with {}.",
                    entryId, handler.getClass().getSimpleName());
        }

        this.handlers.put(entryId, handler);
        Logger.debug("Registered entry handler: <aqua>{}<reset> -> {}",
                entryId, handler.getClass().getSimpleName());

        registerHandlerSettings(entryId, handler);
    }

    @Override
    public EntryHandler<?> getById(String entryId) {
        return this.handlers.get(entryId);
    }

    @Override
    public Collection<EntryHandler<?>> getAll() {
        return this.handlers.values();
    }

    @Override
    public void clear() {
        this.handlers.clear();
        Logger.info("Cleared all registered entry handlers.");
    }

    private boolean registerEntryHandler(Class<? extends EntryHandler<?>> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            Logger.debug("Class {} is abstract or an interface. Skipping.", clazz.getSimpleName());
            return false;
        }

        if (!VersionFilter.passes(clazz, clazz.getSimpleName())) {
            return false;
        }

        try {
            AutoEntry meta = clazz.getAnnotation(AutoEntry.class);
            String entryId = meta.value();

            if (this.handlers.containsKey(entryId)) {
                Logger.warning("Entry ID <yellow>{}<reset> is already registered. Skipping class {}.",
                        entryId, clazz.getSimpleName());
                return false;
            }

            EntryHandler<?> handler = instantiateHandler(clazz);

            this.handlers.put(entryId, handler);
            Logger.debug("Registered entry handler: <aqua>{}<reset> -> {}", entryId, clazz.getSimpleName());

            registerHandlerSettings(entryId, handler);

            return true;

        } catch (LinkageError e) {
            Logger.warning("Skipping EntryHandler <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
            return false;
        } catch (Exception e) {
            Logger.severe("Failed to instantiate entry handler: {}", e, clazz.getName());
            return false;
        }
    }

    private EntryHandler<?> instantiateHandler(Class<? extends EntryHandler<?>> clazz) throws Exception {
        try {
            return clazz.getDeclaredConstructor(ItemsPlugin.class).newInstance(this.plugin);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(
                        "Entry handler " + clazz.getSimpleName() +
                                " must have either a no-args constructor or a constructor with ItemsPlugin parameter."
                );
            }
        }
    }

    private void registerHandlerSettings(String entryId, EntryHandler<?> handler) {
        Class<? extends EntrySettings> settingsClass = handler.settingsType();
        if (settingsClass != null) {
            try {
                PolymorphicRegistry<EntrySettings> registry = PolymorphicRegistry.get(EntrySettings.class);
                if (registry.get(entryId).isPresent()) {
                    Logger.debug("EntrySettings class {} already registered.", settingsClass.getSimpleName());
                    return;
                }

                registry.register(entryId, settingsClass);
                Logger.debug("Registered EntrySettings class: <aqua>{}<reset>", settingsClass.getSimpleName());
            } catch (Exception e) {
                Logger.severe("Failed to register EntrySettings class: {}", e, settingsClass.getName());
            }
        }
    }
}
