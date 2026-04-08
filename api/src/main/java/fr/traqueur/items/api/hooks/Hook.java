package fr.traqueur.items.api.hooks;

/**
 * Represents a plugin integration hook for zItems.
 *
 * <p>Hooks provide integration with third-party Bukkit/Spigot plugins by registering
 * custom extractors, effects, and other components specific to that plugin. They are
 * automatically discovered and loaded at plugin startup if the target plugin is present.</p>
 *
 * <h2>Hook Discovery</h2>
 * <p>Hooks are discovered through the {@link fr.traqueur.items.api.annotations.AutoHook}
 * annotation. Classes annotated with {@code @AutoHook("PluginName")} are automatically
 * scanned and registered during plugin initialization.</p>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>Plugin scans configured packages for {@code @AutoHook} annotations</li>
 *   <li>Hook instances are created and registered in {@link fr.traqueur.items.api.registries.HooksRegistry}</li>
 *   <li>{@link #onEnable()} is called for each registered hook</li>
 *   <li>Hook registers its components (extractors, effects, etc.)</li>
 * </ol>
 *
 * <h2>Example Implementation</h2>
 * <pre>{@code
 * @AutoHook("Jobs")
 * public class JobsHook implements Hook {
 *
 *     @Override
 *     public void onEnable() {
 *         // Register custom extractors for Jobs events
 *         ExtractorsRegistry extractors = Registry.get(ExtractorsRegistry.class);
 *         extractors.register(JobsExpGainEvent.class, new JobsExpExtractor());
 *
 *         // Register Jobs-specific effects
 *         HandlersRegistry handlers = Registry.get(HandlersRegistry.class);
 *         handlers.register("JOBS_BOOST", new JobsBoostHandler());
 *     }
 * }
 * }</pre>
 *
 * <h2>Common Use Cases</h2>
 * <ul>
 *   <li><b>Event Extractors:</b> Extract ItemStack from plugin-specific events</li>
 *   <li><b>Custom Effects:</b> Register effect handlers that interact with the plugin</li>
 *   <li><b>Block Providers:</b> Integrate custom block systems (ItemsAdder, Oraxen, etc.)</li>
 *   <li><b>Economy Integration:</b> Connect shop systems for AutoSell effects</li>
 * </ul>
 *
 * <h2>Module Structure</h2>
 * <p>Hooks are typically organized in separate Gradle modules under {@code hooks/PluginName/}.
 * This allows optional compilation and reduces dependencies when the target plugin isn't used.</p>
 *
 * @see fr.traqueur.items.api.annotations.AutoHook
 * @see fr.traqueur.items.api.registries.HooksRegistry
 */
public interface Hook {

    /**
     * Called during the host plugin's {@code onLoad()} phase, before {@code onEnable()}.
     *
     * <p>Use this for early initialization that must happen before any plugin is enabled,
     * such as bootstrapping libraries (e.g. PacketEvents) that require {@code onLoad}-time setup.</p>
     *
     * <p>The default implementation is a no-op; override only when early init is needed.</p>
     */
    default void onLoad() {}

    /**
     * Called when the hook is enabled during plugin initialization.
     *
     * <p>This method is invoked after the hook has been registered in {@link fr.traqueur.items.api.registries.HooksRegistry}.
     * Implementations should use this method to register their components with the appropriate
     * registries.</p>
     *
     * <p><b>Important:</b> This method is only called if the target plugin is present and enabled.
     * The hook class must be annotated with {@link fr.traqueur.items.api.annotations.AutoHook @AutoHook}
     * to be discovered automatically.</p>
     *
     * <p><b>Typical Operations:</b></p>
     * <ul>
     *   <li>Register {@link fr.traqueur.items.api.effects.ItemSourceExtractor} for plugin events</li>
     *   <li>Register {@link fr.traqueur.items.api.effects.EffectHandler} for custom effects</li>
     *   <li>Register {@link fr.traqueur.items.api.blocks.CustomBlockProvider} if applicable</li>
     *   <li>Initialize plugin-specific resources or listeners</li>
     * </ul>
     *
     * @see fr.traqueur.items.api.registries.HooksRegistry#enableAll()
     */
    void onEnable();

    /**
     * Called when the host plugin is disabled.
     *
     * <p>Use this to clean up any resources allocated in {@link #onLoad()} or {@link #onEnable()}.
     * The default implementation is a no-op.</p>
     */
    default void onDisable() {}

}
