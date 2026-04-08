package fr.traqueur.items.api.managers;

import fr.traqueur.items.api.ItemsPlugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Base interface for all service managers in the zItems plugin system.
 *
 * <p>Managers are singleton service objects that provide high-level API functionality
 * for specific subsystems. They are registered via Bukkit's {@code ServicesManager}
 * during plugin initialization and can be retrieved by external plugins.</p>
 *
 * <h2>Sealed Hierarchy</h2>
 * <p>This is a sealed interface, restricting implementations to:</p>
 * <ul>
 *   <li>{@link EffectsManager} - Effect application and management</li>
 *   <li>{@link ItemsManager} - Custom item building and recipes</li>
 * </ul>
 *
 * <h2>Service Registration</h2>
 * <p>Managers are registered as Bukkit services during plugin startup:</p>
 * <pre>{@code
 * // In plugin onEnable()
 * ServicesManager services = getServer().getServicesManager();
 * services.register(EffectsManager.class, new ZEffectsManager(), plugin, ServicePriority.Normal);
 * services.register(ItemsManager.class, new ZItemsManager(), plugin, ServicePriority.Normal);
 * }</pre>
 *
 * <h2>Accessing Managers</h2>
 * <p>External plugins can access managers via Bukkit's service system:</p>
 * <pre>{@code
 * // From another plugin
 * ServicesManager services = Bukkit.getServicesManager();
 * EffectsManager effectsManager = services.load(EffectsManager.class);
 *
 * if (effectsManager != null) {
 *     // Use the manager
 *     effectsManager.applyEffect(player, item, effect);
 * }
 * }</pre>
 *
 * <p>Within zItems code, managers can be accessed directly:</p>
 * <pre>{@code
 * ItemsManager itemsManager = (ItemsManager) Bukkit.getServicesManager()
 *     .load(ItemsManager.class);
 * }</pre>
 *
 * <h2>Design Pattern</h2>
 * <p>Managers follow the Service Locator pattern, providing a centralized point
 * of access for major plugin subsystems. This allows:</p>
 * <ul>
 *   <li>Clean separation between API interfaces and implementation</li>
 *   <li>Easy mocking and testing via interface contracts</li>
 *   <li>External plugin integration without direct dependencies</li>
 *   <li>Runtime service discovery and optional soft dependencies</li>
 * </ul>
 *
 * @see EffectsManager
 * @see ItemsManager
 * @see org.bukkit.plugin.ServicesManager
 */
public sealed interface Manager permits EffectsManager, ItemsManager, DurabilityManager {

    /**
     * Retrieves the main {@link ItemsPlugin} instance.
     *
     * <p>This convenience method provides access to the plugin instance from
     * any manager. It uses Bukkit's {@link JavaPlugin#getPlugin(Class)} method
     * to retrieve the singleton instance.</p>
     *
     * <p><b>Note:</b> This method assumes the plugin is loaded and enabled.
     * It will throw {@link IllegalStateException} if called before the plugin
     * is properly initialized.</p>
     *
     * @return the zItems plugin instance
     * @throws IllegalStateException if the plugin is not loaded
     */
    default ItemsPlugin getPlugin() {
        return JavaPlugin.getPlugin(ItemsPlugin.class);
    }

}
