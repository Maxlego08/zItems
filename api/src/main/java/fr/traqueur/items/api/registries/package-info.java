/**
 * Registry system for managing plugin components.
 *
 * <p>This package provides type-safe registries for storing and retrieving plugin
 * components like items, effects, handlers, hooks, and more. All registries follow
 * a common {@link fr.traqueur.items.api.registries.Registry} interface pattern.</p>
 *
 * <h2>Core Registries</h2>
 *
 * <h3>Items and Effects</h3>
 * <ul>
 *   <li>{@link fr.traqueur.items.api.registries.ItemsRegistry} - Custom item definitions from items/*.yml</li>
 *   <li>{@link fr.traqueur.items.api.registries.EffectsRegistry} - Effect instances from effects/*.yml</li>
 *   <li>{@link fr.traqueur.items.api.registries.HandlersRegistry} - Effect handler implementations</li>
 *   <li>{@link fr.traqueur.items.api.registries.ApplicatorsRegistry} - Effect application methods</li>
 * </ul>
 *
 * <h3>Events and Extraction</h3>
 * <ul>
 *   <li>{@link fr.traqueur.items.api.registries.ExtractorsRegistry} - ItemStack extractors from events</li>
 * </ul>
 *
 * <h3>Plugin Integrations</h3>
 * <ul>
 *   <li>{@link fr.traqueur.items.api.registries.HooksRegistry} - Third-party plugin hooks</li>
 *   <li>{@link fr.traqueur.items.api.registries.LocationAccessRegistry} - Region protection providers</li>
 *   <li>{@link fr.traqueur.items.api.registries.CustomBlockProviderRegistry} - Custom block detection</li>
 * </ul>
 *
 * <h2>Registry Access Pattern</h2>
 * <p>All registries are accessed via the static {@link fr.traqueur.items.api.registries.Registry#get(Class)}
 * method using Guava's {@code ClassToInstanceMap}:</p>
 * <pre>{@code
 * // Get a registry
 * ItemsRegistry items = Registry.get(ItemsRegistry.class);
 * EffectsRegistry effects = Registry.get(EffectsRegistry.class);
 * HandlersRegistry handlers = Registry.get(HandlersRegistry.class);
 *
 * // Use the registry
 * Optional<Item> item = items.get("custom_sword");
 * Optional<Effect> effect = effects.get("hammer");
 * Optional<EffectHandler> handler = handlers.get("HAMMER");
 * }</pre>
 *
 * <h2>Common Registry Operations</h2>
 *
 * <h3>Registration</h3>
 * <pre>{@code
 * ItemsRegistry registry = Registry.get(ItemsRegistry.class);
 * registry.register("my_item", myItem);
 * }</pre>
 *
 * <h3>Retrieval</h3>
 * <pre>{@code
 * Optional<Item> item = registry.get("my_item");
 * item.ifPresent(i -> {
 *     // Use the item
 * });
 * }</pre>
 *
 * <h3>Iteration</h3>
 * <pre>{@code
 * // Iterate over all values
 * registry.values().forEach(item -> {
 *     // Process each item
 * });
 *
 * // Iterate over entries
 * registry.entrySet().forEach(entry -> {
 *     String id = entry.getKey();
 *     Item item = entry.getValue();
 * });
 * }</pre>
 *
 * <h2>File-Based Registries</h2>
 * <p>Some registries support loading components from YAML files via {@code FileBasedRegistry}
 * (available in the common module):</p>
 * <ul>
 *   <li>{@link fr.traqueur.items.api.registries.ItemsRegistry} - Loads from items/ directory</li>
 *   <li>{@link fr.traqueur.items.api.registries.EffectsRegistry} - Loads from effects/ directory</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p><b>Warning:</b> Registries are <b>not thread-safe</b>. Registration and access
 * should occur on the main server thread during plugin initialization and reload.</p>
 *
 * @see fr.traqueur.items.api.registries.Registry
 */
package fr.traqueur.items.api.registries;