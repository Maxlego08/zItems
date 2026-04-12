/**
 * Core API package for the zItems plugin.
 *
 * <p>This package contains the main plugin class and fundamental interfaces used
 * throughout the zItems system. It serves as the entry point for both the plugin
 * implementation and external integrations.</p>
 *
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link fr.traqueur.items.api.ItemsPlugin} - Main plugin class and service provider</li>
 *   <li>{@link fr.traqueur.items.api.Logger} - Centralized logging utilities</li>
 * </ul>
 *
 * <h2>Sub-packages</h2>
 * <ul>
 *   <li>{@link fr.traqueur.items.api.managers} - Service managers for high-level operations</li>
 *   <li>{@link fr.traqueur.items.api.registries} - Component registries (items, effects, handlers)</li>
 *   <li>{@link fr.traqueur.items.api.effects} - Effect system (handlers, dispatcher, context)</li>
 *   <li>{@link fr.traqueur.items.api.items} - Custom item definitions and builders</li>
 *   <li>{@link fr.traqueur.items.api.blocks} - Block tracking and custom block providers</li>
 *   <li>{@link fr.traqueur.items.api.hooks} - Third-party plugin integration system</li>
 *   <li>{@link fr.traqueur.items.api.settings} - YAML configuration classes</li>
 *   <li>{@link fr.traqueur.items.api.events} - Custom Bukkit events</li>
 *   <li>{@link fr.traqueur.items.api.annotations} - Annotation-driven component discovery</li>
 * </ul>
 *
 * <h2>External Plugin Usage</h2>
 * <pre>{@code
 * // Add zItems API as a dependency in your plugin
 * public class MyPlugin extends JavaPlugin {
 *     @Override
 *     public void onEnable() {
 *         // Access managers via Bukkit services
 *         EffectsManager effectsManager = (EffectsManager) getServer()
 *             .getServicesManager()
 *             .load(EffectsManager.class);
 *
 *         ItemsManager itemsManager = (ItemsManager) getServer()
 *             .getServicesManager()
 *             .load(ItemsManager.class);
 *
 *         // Use the API
 *         effectsManager.applyEffect(player, item, effect);
 *     }
 * }
 * }</pre>
 *
 * @see fr.traqueur.items.api.ItemsPlugin
 * @see fr.traqueur.items.api.managers.Manager
 * @see fr.traqueur.items.api.registries.Registry
 */
package fr.traqueur.items.api;