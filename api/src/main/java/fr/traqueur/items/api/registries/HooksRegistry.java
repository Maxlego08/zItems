package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.hooks.Hook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * Registry for managing {@link Hook} instances that provide third-party plugin integrations.
 *
 * <p>This registry automatically discovers and manages hooks annotated with
 * {@link fr.traqueur.items.api.annotations.AutoHook @AutoHook}. Hooks are only
 * loaded if their target plugin is present on the server.</p>
 *
 * <h2>Hook Discovery Process</h2>
 * <ol>
 *   <li>{@link #scanPackage(JavaPlugin, String)} is called during plugin initialization</li>
 *   <li>All classes annotated with {@code @AutoHook("PluginName")} are found</li>
 *   <li>For each hook, the target plugin presence is checked via Bukkit's PluginManager</li>
 *   <li>If the plugin exists, the hook is instantiated and registered with the plugin name as key</li>
 *   <li>{@link #enableAll()} is called to invoke {@link Hook#onEnable()} on all registered hooks</li>
 * </ol>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // During plugin initialization
 * HooksRegistry hooksRegistry = Registry.get(HooksRegistry.class);
 *
 * // Scan for hooks in plugin packages
 * hooksRegistry.scanPackage(plugin, "fr.traqueur.items");
 *
 * // Enable all discovered hooks
 * hooksRegistry.enableAll();
 *
 * // Later, check if a specific hook is loaded by name
 * Optional<Hook> jobsHook = hooksRegistry.get("Jobs");
 * if (jobsHook.isPresent()) {
 *     // Jobs integration is available
 * }
 *
 * // Or by class type (type-safe)
 * Optional<JobsHook> jobsHook = hooksRegistry.getHook(JobsHook.class);
 * if (jobsHook.isPresent()) {
 *     jobsHook.get().doSomethingSpecific();
 * }
 * }</pre>
 *
 * <h2>Registry Keys</h2>
 * <p>Hooks are stored using the target plugin name as the key (as specified in the
 * {@code @AutoHook} annotation). For example, a hook with {@code @AutoHook("WorldGuard")}
 * is registered with the key {@code "WorldGuard"}.</p>
 *
 * @see Hook
 * @see fr.traqueur.items.api.annotations.AutoHook
 */
public interface HooksRegistry extends Registry<String, Hook> {

    /**
     * Enables all registered hooks by calling their {@link Hook#onEnable()} method.
     *
     * <p>This method should be called after all hooks have been discovered and registered
     * via {@link #scanPackage(JavaPlugin, String)}. It iterates through all registered
     * hooks and invokes their initialization logic.</p>
     *
     * <p><b>Execution Order:</b> Hooks are enabled in the order they were registered,
     * which depends on classpath scanning order and is not guaranteed to be deterministic.
     * Hooks should not depend on other hooks being initialized first.</p>
     *
     * <p><b>Error Handling:</b> If a hook's {@code onEnable()} method throws an exception,
     * the error is logged but does not prevent other hooks from being enabled.</p>
     *
     * @see Hook#onEnable()
     */
    /**
     * Calls {@link Hook#onLoad()} on all registered hooks whose target plugin is present.
     * Should be invoked from the host plugin's {@code onLoad()} after {@link #scanPackage}.
     */
    void loadAll();

    void enableAll();

    /**
     * Calls {@link Hook#onDisable()} on all hooks that were successfully enabled.
     * Should be invoked from the host plugin's {@code onDisable()}.
     */
    void disableAll();

    /**
     * Scans a package for classes annotated with {@link fr.traqueur.items.api.annotations.AutoHook @AutoHook}
     * and registers them as hooks.
     *
     * <p>This method uses reflection to scan the specified package and all sub-packages
     * for classes with the {@code @AutoHook} annotation. For each discovered class:</p>
     * <ol>
     *   <li>The target plugin name is extracted from the annotation</li>
     *   <li>Checks if the target plugin is loaded via {@code plugin.getServer().getPluginManager()}</li>
     *   <li>If loaded, instantiates the hook class (must have a no-args constructor)</li>
     *   <li>Registers the hook instance with the plugin name as the key</li>
     * </ol>
     *
     * <p><b>Package Scanning:</b> The scan is recursive and includes all sub-packages.
     * For example, scanning {@code "fr.traqueur.items"} will find hooks in
     * {@code "fr.traqueur.items.jobs"}, {@code "fr.traqueur.items.worldguard"}, etc.</p>
     *
     * <p><b>Performance:</b> Package scanning is performed at plugin startup using the
     * Reflections library. Large packages may increase startup time slightly.</p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * // Scan the main plugin package and all hook modules
     * hooksRegistry.scanPackage(plugin, "fr.traqueur.items");
     *
     * // This will discover hooks like:
     * // - @AutoHook("Jobs") in fr.traqueur.items.jobs.JobsHook
     * // - @AutoHook("WorldGuard") in fr.traqueur.items.worldguard.WorldGuardHook
     * // - etc.
     * }</pre>
     *
     * @param plugin the plugin instance used to check for target plugin availability
     * @param packageName the base package name to scan (e.g., "fr.traqueur.items")
     * @throws IllegalArgumentException if the package name is invalid or not found
     */
    void scanPackage(JavaPlugin plugin, String packageName);

}
