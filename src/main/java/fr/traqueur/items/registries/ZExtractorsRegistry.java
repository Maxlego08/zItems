package fr.traqueur.items.registries;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.VersionFilter;
import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import fr.traqueur.items.api.registries.ExtractorsRegistry;
import fr.traqueur.items.utils.ReflectionsCache;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Registry for ItemSourceExtractors with hierarchical resolution.
 * <p>
 * This registry supports both specific extractors for individual event types
 * and generic extractors for event hierarchies. When an extractor is requested
 * for an event type, the registry will:
 * <ol>
 *   <li>Look for an exact match (e.g., PlayerInteractExtractor for PlayerInteractEvent)</li>
 *   <li>Walk up the class hierarchy to find a generic extractor (e.g., PlayerEventExtractor for PlayerEvent)</li>
 *   <li>Cache the result to avoid repeated reflection lookups</li>
 * </ol>
 * <p>
 * Example hierarchy resolution:
 * <pre>
 * PlayerInteractEvent → PlayerInteractExtractor (exact match)
 * PlayerMoveEvent → PlayerEventExtractor (parent match)
 * CustomPlayerEvent → PlayerEventExtractor (parent match)
 * </pre>
 */
public class ZExtractorsRegistry implements ExtractorsRegistry {

    private final ItemsPlugin plugin;
    private final Map<Class<? extends Event>, ItemSourceExtractor<?>> extractors;
    private final Map<Class<? extends Event>, ItemSourceExtractor<?>> cache;
    private final Set<String> scannedPackages;

    public ZExtractorsRegistry(ItemsPlugin plugin) {
        this.plugin = plugin;
        this.extractors = new HashMap<>();
        this.cache = new HashMap<>();
        this.scannedPackages = new HashSet<>();
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

        Logger.info("Scanning package <aqua>{}<reset> for ItemSourceExtractors...", packageName);

        try {
            Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, packageName);

            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(AutoExtractor.class);

            int count = 0;
            for (Class<?> clazz : annotatedClasses) {
                if (!ItemSourceExtractor.class.isAssignableFrom(clazz)) {
                    Logger.warning("Class <yellow>{}<reset> is annotated with @ExtractorMeta but does not implement ItemSourceExtractor. Skipping.",
                            clazz.getSimpleName());
                    continue;
                }
                //noinspection unchecked
                if (registerExtractor((Class<? extends ItemSourceExtractor<?>>) clazz)) {
                    count++;
                }
            }

            scannedPackages.add(packageName);
            Logger.info("Registered <gold>{}<reset> ItemSourceExtractor(s) from package {}.", count, packageName);

        } catch (Exception e) {
            Logger.severe("Failed to scan package {}: {}", e, packageName);
        }
    }

    @Override
    public Set<String> getScannedPackages() {
        return Collections.unmodifiableSet(scannedPackages);
    }

    /**
     * Registers a single extractor class.
     * Tries to instantiate using a constructor with ItemsPlugin parameter first,
     * then falls back to a no-args constructor if not available.
     *
     * @return true if successfully registered, false otherwise
     */
    private boolean registerExtractor(Class<? extends ItemSourceExtractor<?>> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            Logger.debug("Class {} is abstract or an interface. Skipping.", clazz.getSimpleName());
            return false;
        }

        if (!VersionFilter.passes(clazz, clazz.getSimpleName())) {
            return false;
        }

        try {
            AutoExtractor meta = clazz.getAnnotation(AutoExtractor.class);
            Class<? extends Event> eventType = meta.value();

            if (this.extractors.containsKey(eventType)) {
                Logger.warning("Event type <yellow>{}<reset> already has a registered extractor. Skipping class {}.",
                        eventType.getSimpleName(), clazz.getSimpleName());
                return false;
            }

            ItemSourceExtractor<?> extractor = instantiateExtractor(clazz);

            this.extractors.put(eventType, extractor);
            Logger.debug("Registered ItemSourceExtractor: <aqua>{}<reset> -> {}", eventType.getSimpleName(), clazz.getSimpleName());

            return true;

        } catch (LinkageError e) {
            Logger.warning("Skipping ItemSourceExtractor <yellow>{}<reset> — missing API on this server: {}", clazz.getSimpleName(), e.getMessage());
            return false;
        } catch (Exception e) {
            Logger.severe("Failed to instantiate ItemSourceExtractor: {}", e, clazz.getName());
            return false;
        }
    }

    /**
     * Instantiates an extractor using the appropriate constructor.
     * Tries constructor with ItemsPlugin parameter first, then no-args constructor.
     *
     * @param clazz the extractor class to instantiate
     * @return the instantiated extractor
     * @throws Exception if instantiation fails
     */
    private ItemSourceExtractor<?> instantiateExtractor(Class<? extends ItemSourceExtractor<?>> clazz) throws Exception {
        // Try constructor with ItemsPlugin parameter first
        try {
            return clazz.getDeclaredConstructor(ItemsPlugin.class).newInstance(this.plugin);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException ex) {
                throw new NoSuchMethodException(
                        "ItemSourceExtractor " + clazz.getSimpleName() +
                                " must have either a no-args constructor or a constructor with ItemsPlugin parameter."
                );
            }
        }
    }

    /**
     * Checks if an extractor exists for the given event type (exact or hierarchical).
     *
     * @param eventType the event class to check
     * @return true if an extractor can be resolved, false otherwise
     */
    @Override
    public boolean has(Class<? extends Event> eventType) {
        return getById(eventType) != null;
    }

    @Override
    public void register(Class<? extends Event> aClass, ItemSourceExtractor<?> item) {
        extractors.put(aClass, item);
        // Clear cache for this event type since we're changing the extractor
        cache.remove(aClass);
        Logger.debug("Registered ItemSourceExtractor for: <aqua>{}<reset>", aClass.getSimpleName());
    }

    @Override
    public ItemSourceExtractor<?> getById(Class<? extends Event> eventType) {
        // 1. Check cache first
        ItemSourceExtractor<?> cached = cache.get(eventType);
        if (cached != null) {
            return cached;
        }

        // 2. Look for exact match
        ItemSourceExtractor<?> extractor = extractors.get(eventType);
        if (extractor != null) {
            cache.put(eventType, extractor);
            return extractor;
        }

        // 3. Walk up the class hierarchy
        Class<?> currentClass = eventType.getSuperclass();
        while (currentClass != null && Event.class.isAssignableFrom(currentClass)) {
            extractor = extractors.get(currentClass);
            if (extractor != null) {
                Logger.debug("Using fallback extractor <yellow>{}<reset> for event <aqua>{}<reset>",
                        currentClass.getSimpleName(), eventType.getSimpleName());
                cache.put(eventType, extractor);
                return extractor;
            }
            currentClass = currentClass.getSuperclass();
        }

        // 4. No extractor found
        Logger.debug("No extractor found for event type: {}", eventType.getSimpleName());
        return null;
    }

    @Override
    public Collection<ItemSourceExtractor<?>> getAll() {
        return extractors.values();
    }

    @Override
    public void clear() {
        extractors.clear();
        cache.clear();
        scannedPackages.clear();
        Logger.debug("Cleared all registered ItemSourceExtractors and cache.");
    }
}