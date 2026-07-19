package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.effects.entries.EntryHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

/**
 * Registry for EntryHandlers.
 * <p>
 * Mirrors {@link HandlersRegistry}, but for {@link EntryHandler}s — discovers
 * classes annotated with {@code @AutoEntry}, instantiates them, registers them
 * by their entry type id, and manages the polymorphic EntrySettings registration.
 */
public interface EntryHandlersRegistry extends Registry<String, EntryHandler<?>> {

    /**
     * Scans a package for classes annotated with {@code @AutoEntry} and registers them.
     *
     * @param plugin      the plugin instance (used for instantiation and classloader)
     * @param packageName the package to scan
     */
    void scanPackage(JavaPlugin plugin, String packageName);

    /**
     * Gets all packages that have been scanned by this registry.
     *
     * @return an unmodifiable set of package names
     */
    Set<String> getScannedPackages();
}
