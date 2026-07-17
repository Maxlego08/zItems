package fr.traqueur.items.registries;

import fr.traqueur.items.VersionFilter;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoExit;
import fr.traqueur.items.api.exit.ExitHandler;
import fr.traqueur.items.api.exit.ExitSettings;
import fr.traqueur.items.api.registries.ExitHandlersRegistry;
import fr.traqueur.items.utils.ReflectionsCache;
import fr.traqueur.structura.registries.PolymorphicRegistry;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of ExitHandlersRegistry that discovers and registers ExitHandlers.
 * <p>
 * Mirrors {@link ZHandlersRegistry}/{@link ZEntryHandlersRegistry}, but for {@link ExitHandler}s.
 */
public class ZExitHandlersRegistry implements ExitHandlersRegistry {

    private final ItemsPlugin plugin;
    private final Map<String, ExitHandler<?>> handlers;
    private final Set<String> scannedPackages;

    public ZExitHandlersRegistry(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.handlers = new HashMap<>();
        this.scannedPackages = new HashSet<>();

        // Create the polymorphic registry for ExitSettings
        PolymorphicRegistry.create(ExitSettings.class, registry -> {
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

        Logger.info("Scanning package <aqua>{}<reset> for ExitHandlers...", packageName);

        try {
            Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, packageName);

            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoExit.class);

            int count = 0;
            for (Class<?> clazz : annotatedClasses) {
                if (!ExitHandler.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @AutoExit but does not implement ExitHandler. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                //noinspection unchecked
                if (registerExitHandler((Class<? extends ExitHandler<?>>) clazz)) {
                    count++;
                }
            }

            scannedPackages.add(packageName);
            Logger.info("Registered <gold>{}<reset> exit handler(s) from package {}.", count, packageName);

        } catch (Exception e) {
            Logger.severe("Failed to scan package {}: {}", e, packageName);
        }
    }

    @Override
    public Set<String> getScannedPackages() {
        return Collections.unmodifiableSet(scannedPackages);
    }

    @Override
    public void register(String exitId, ExitHandler<?> handler) {
        if (exitId == null || exitId.trim().isEmpty()) {
            Logger.severe("Cannot register handler with null or empty exit ID.");
            return;
        }

        if (handler == null) {
            Logger.severe("Cannot register null handler for exit ID: {}", exitId);
            return;
        }

        if (this.handlers.containsKey(exitId)) {
            Logger.warning("Exit ID <yellow>{}<reset> is already registered. Overwriting with {}.",
                    exitId, handler.getClass().getSimpleName());
        }

        this.handlers.put(exitId, handler);
        Logger.debug("Registered exit handler: <aqua>{}<reset> -> {}",
                exitId, handler.getClass().getSimpleName());

        registerHandlerSettings(exitId, handler);
    }

    @Override
    public ExitHandler<?> getById(String exitId) {
        return this.handlers.get(exitId);
    }

    @Override
    public Collection<ExitHandler<?>> getAll() {
        return this.handlers.values();
    }

    @Override
    public void clear() {
        this.handlers.clear();
        Logger.info("Cleared all registered exit handlers.");
    }

    private boolean registerExitHandler(Class<? extends ExitHandler<?>> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            Logger.debug("Class {} is abstract or an interface. Skipping.", clazz.getSimpleName());
            return false;
        }

        if (!VersionFilter.passes(clazz, clazz.getSimpleName())) {
            return false;
        }

        try {
            AutoExit meta = clazz.getAnnotation(AutoExit.class);
            String exitId = meta.value();

            if (this.handlers.containsKey(exitId)) {
                Logger.warning("Exit ID <yellow>{}<reset> is already registered. Skipping class {}.",
                        exitId, clazz.getSimpleName());
                return false;
            }

            ExitHandler<?> handler = instantiateHandler(clazz);

            this.handlers.put(exitId, handler);
            Logger.debug("Registered exit handler: <aqua>{}<reset> -> {}", exitId, clazz.getSimpleName());

            registerHandlerSettings(exitId, handler);

            return true;

        } catch (LinkageError e) {
            Logger.warning("Skipping ExitHandler <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
            return false;
        } catch (Exception e) {
            Logger.severe("Failed to instantiate exit handler: {}", e, clazz.getName());
            return false;
        }
    }

    private ExitHandler<?> instantiateHandler(Class<? extends ExitHandler<?>> clazz) throws Exception {
        try {
            return clazz.getDeclaredConstructor(ItemsPlugin.class).newInstance(this.plugin);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(
                        "Exit handler " + clazz.getSimpleName() +
                                " must have either a no-args constructor or a constructor with ItemsPlugin parameter."
                );
            }
        }
    }

    private void registerHandlerSettings(String exitId, ExitHandler<?> handler) {
        Class<? extends ExitSettings> settingsClass = handler.settingsType();
        if (settingsClass != null) {
            try {
                PolymorphicRegistry<ExitSettings> registry = PolymorphicRegistry.get(ExitSettings.class);
                if (registry.get(exitId).isPresent()) {
                    Logger.debug("ExitSettings class {} already registered.", settingsClass.getSimpleName());
                    return;
                }

                registry.register(exitId, settingsClass);
                Logger.debug("Registered ExitSettings class: <aqua>{}<reset>", settingsClass.getSimpleName());
            } catch (Exception e) {
                Logger.severe("Failed to register ExitSettings class: {}", e, settingsClass.getName());
            }
        }
    }
}
