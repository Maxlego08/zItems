package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

/**
 * Registry for ItemSourceExtractors with hierarchical resolution.
 * <p>
 * This registry supports both specific extractors for individual event types
 * and generic extractors for event hierarchies. It provides automatic discovery
 * via package scanning using @ExtractorMeta annotations.
 * <p>
 * Example usage:
 * <pre>{@code
 * ExtractorsRegistry registry = Registry.get(ExtractorsRegistry.class);
 *
 * // Scan a package for extractors
 * registry.scanPackage(plugin, "fr.traqueur.items.effects.extractors");
 *
 * // Get an extractor for an event type
 * ItemSourceExtractor<?> extractor = registry.getById(BlockBreakEvent.class);
 * }</pre>
 */
public interface ExtractorsRegistry extends Registry<Class<? extends Event>, ItemSourceExtractor<?>> {

    /**
     * Scans a package for classes annotated with @ExtractorMeta and registers them.
     * <p>
     * This method will:
     * <ol>
     *   <li>Find all classes annotated with @ExtractorMeta in the package</li>
     *   <li>Instantiate extractors (tries constructor with JavaPlugin, then no-args)</li>
     *   <li>Register the extractor for its event type</li>
     * </ol>
     *
     * @param plugin      the plugin instance (used for instantiation and classloader)
     * @param packageName the package to scan (e.g., "fr.traqueur.items.effects.extractors")
     */
    void scanPackage(JavaPlugin plugin, String packageName);

    /**
     * Gets all packages that have been scanned by this registry.
     *
     * @return an unmodifiable set of package names
     */
    Set<String> getScannedPackages();

    /**
     * Checks if an extractor exists for the given event type (exact or hierarchical).
     *
     * @param eventType the event class to check
     * @return true if an extractor can be resolved, false otherwise
     */
    boolean has(Class<? extends Event> eventType);

    /**
     * Returns every event type an extractor has been explicitly registered for.
     * <p>
     * Used by the dynamic listener registration to determine which events must be
     * listened to on behalf of {@link fr.traqueur.items.api.effects.EffectHandler.AnyEventEffectHandler}s,
     * which declare no event affinity of their own.
     *
     * @return the set of event types with a registered extractor
     */
    Set<Class<? extends Event>> registeredEventTypes();
}
