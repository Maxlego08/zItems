package fr.traqueur.items.api.settings;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import fr.traqueur.items.api.settings.models.RecipeWrapper;
import fr.traqueur.structura.api.Loadable;

/**
 * Base interface for all configuration settings classes in zItems.
 *
 * <p>Settings classes represent YAML configuration files deserialized into Java objects
 * using the Structura framework. They extend {@link Loadable} to enable automatic
 * deserialization and provide a static registry for singleton access.</p>
 *
 * <h2>Purpose</h2>
 * <p>This interface serves multiple purposes:</p>
 * <ul>
 *   <li><b>Marker Interface:</b> Identifies classes as configuration settings</li>
 *   <li><b>Registry Access:</b> Provides static storage for singleton settings instances</li>
 *   <li><b>Structura Integration:</b> Extends {@link Loadable} for YAML deserialization</li>
 *   <li><b>Type Safety:</b> Uses Guava's {@link ClassToInstanceMap} for type-safe retrieval</li>
 * </ul>
 *
 * <h2>Settings Hierarchy</h2>
 * <p>Settings classes in zItems include:</p>
 * <ul>
 *   <li>{@code ItemSettings} - Custom item definitions (items/*.yml)</li>
 *   <li>{@link RecipeWrapper} - Recipe configurations</li>
 *   <li>{@link fr.traqueur.items.api.effects.EffectSettings} - Effect handler configurations (effects/*.yml)</li>
 * </ul>
 *
 * <h2>Static Registry Pattern</h2>
 * <p>Settings instances are stored in a static {@link ClassToInstanceMap} to enable
 * singleton access from anywhere in the codebase:</p>
 * <pre>{@code
 * // During plugin initialization (loading config.yml, messages.yml, etc.)
 * ConfigSettings config = structura.load(ConfigSettings.class, configFile);
 * Settings.register(ConfigSettings.class, config);
 *
 * MessagesSettings messages = structura.load(MessagesSettings.class, messagesFile);
 * Settings.register(MessagesSettings.class, messages);
 *
 * // Later, anywhere in the code
 * ConfigSettings config = Settings.get(ConfigSettings.class);
 * boolean debugMode = config.isDebug();
 *
 * MessagesSettings messages = Settings.get(MessagesSettings.class);
 * String prefix = messages.getPrefix();
 * }</pre>
 *
 * <h2>Structura Integration</h2>
 * <p>By extending {@link Loadable}, settings classes can be automatically deserialized
 * from YAML files. The Structura framework handles:</p>
 * <ul>
 *   <li>Type conversion (String → Material, Sound, etc.)</li>
 *   <li>Custom readers for Bukkit types</li>
 *   <li>Nested object deserialization</li>
 *   <li>List and map handling</li>
 * </ul>
 *
 * <h2>Example Settings Class</h2>
 * <pre>{@code
 * public class ConfigSettings implements Settings {
 *     private boolean debug;
 *     private int defaultEffectView;
 *     private List<String> disabledWorlds;
 *
 *     // Getters (required for Structura)
 *     public boolean isDebug() { return debug; }
 *     public int getDefaultEffectView() { return defaultEffectView; }
 *     public List<String> getDisabledWorlds() { return disabledWorlds; }
 *
 *     // Structura lifecycle method (optional)
 *     @Override
 *     public void afterLoad() {
 *         // Post-processing after YAML load
 *         if (disabledWorlds == null) {
 *             disabledWorlds = new ArrayList<>();
 *         }
 *     }
 * }
 *
 * // Corresponding YAML (config.yml)
 * debug: false
 * default-effect-view: -1
 * disabled-worlds:
 *   - world_nether
 *   - world_the_end
 * }</pre>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>Plugin loads YAML file</li>
 *   <li>Structura deserializes YAML into settings object</li>
 *   <li>Settings object is registered via {@link #register(Class, Settings)}</li>
 *   <li>Settings can be retrieved anywhere via {@link #get(Class)}</li>
 *   <li>On reload, new instance replaces old in registry</li>
 * </ol>
 *
 * <h2>Thread Safety</h2>
 * <p><b>Warning:</b> The static registry is <b>not thread-safe</b>. Settings should
 * only be registered and accessed from the main server thread during plugin
 * initialization and reload operations.</p>
 *
 * @see Loadable
 * @see RecipeWrapper
 * @see fr.traqueur.items.api.effects.EffectSettings
 */
public interface Settings extends Loadable {

    /**
     * Internal static storage for settings instances.
     *
     * <p>This map provides type-safe storage and retrieval of settings singletons.
     * Each settings class can have at most one registered instance.</p>
     *
     * <p><b>Implementation Note:</b> Uses Guava's {@link MutableClassToInstanceMap}
     * which ensures type safety at both compile-time and runtime.</p>
     */
    ClassToInstanceMap<Settings> INSTANCES = MutableClassToInstanceMap.create();

    /**
     * Retrieves the registered settings instance for the specified class.
     *
     * <p>This method provides global access to settings objects that have been
     * registered during plugin initialization.</p>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * // Get configuration settings
     * ConfigSettings config = Settings.get(ConfigSettings.class);
     * if (config.isDebug()) {
     *     plugin.getLogger().info("Debug mode enabled");
     * }
     *
     * // Get messages
     * MessagesSettings messages = Settings.get(MessagesSettings.class);
     * player.sendMessage(messages.getPrefix() + messages.getWelcomeMessage());
     * }</pre>
     *
     * @param clazz the class of the settings to retrieve
     * @param <T>   the type of the settings (must extend Settings)
     * @return the settings instance, or {@code null} if not registered
     * @throws NullPointerException if clazz is null
     */
    static <T extends Settings> T get(Class<T> clazz) {
        return INSTANCES.getInstance(clazz);
    }

    /**
     * Registers a settings instance for global access.
     *
     * <p>This method is typically called during plugin initialization after loading
     * settings from YAML files. If a settings instance for the class already exists,
     * it will be replaced (useful for reload operations).</p>
     *
     * <p><b>Registration Example:</b></p>
     * <pre>{@code
     * // During plugin onEnable() or reload
     * File configFile = new File(getDataFolder(), "config.yml");
     * ConfigSettings config = structura.load(ConfigSettings.class, configFile);
     * Settings.register(ConfigSettings.class, config);
     *
     * File messagesFile = new File(getDataFolder(), "messages.yml");
     * MessagesSettings messages = structura.load(MessagesSettings.class, messagesFile);
     * Settings.register(MessagesSettings.class, messages);
     * }</pre>
     *
     * <p><b>Important:</b> This method should only be called from the main server thread
     * during initialization or reload operations.</p>
     *
     * @param clazz    the class of the settings to register
     * @param instance the settings instance to register
     * @param <T>      the type of the settings (must extend Settings)
     * @throws NullPointerException     if clazz or instance is null
     * @throws IllegalArgumentException if instance is not of type clazz
     */
    static <T extends Settings> void register(Class<T> clazz, T instance) {
        INSTANCES.putInstance(clazz, instance);
    }


}
