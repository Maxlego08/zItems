package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.exit.ExitHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

/**
 * Registry for ExitHandlers.
 * <p>
 * Mirrors {@link HandlersRegistry}/{@link EntryHandlersRegistry}, but for {@link ExitHandler}s.
 */
public interface ExitHandlersRegistry extends Registry<String, ExitHandler<?>> {

    /**
     * Scans a package for classes annotated with {@code @AutoExit} and registers them.
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
