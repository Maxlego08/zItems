package fr.traqueur.items.serialization;

import fr.traqueur.items.api.effects.Effect;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Custom {@link PersistentDataType} for serializing {@link Effect} objects to/from item PDC.
 *
 * <p>This class enables storing effect data directly in ItemStack metadata using Bukkit's
 * {@link org.bukkit.persistence.PersistentDataContainer} system. Effects are serialized
 * as strings (typically JSON) for persistence across server restarts.</p>
 *
 * <h2>Singleton Pattern</h2>
 * <p>This class follows the singleton pattern with a static {@link #INSTANCE} field that
 * is initialized by the plugin implementation during startup. The constructor is protected
 * to prevent external instantiation.</p>
 *
 * <h2>Implementation Requirements</h2>
 * <p>Concrete implementations must:</p>
 * <ol>
 *   <li>Implement methods to serialize/deserialize Effect objects</li>
 *   <li>Initialize the {@link #INSTANCE} field during plugin startup</li>
 *   <li>Handle missing effects gracefully (e.g., after config removal)</li>
 * </ol>
 *
 * @see Effect
 * @see TrackedBlockDataType
 * @see org.bukkit.persistence.PersistentDataType
 */
public abstract class EffectDataType implements PersistentDataType<String, Effect> {

    /**
     * Singleton instance of the EffectDataType implementation.
     *
     * <p>This field is initialized by the plugin implementation during startup.
     * It must be accessed after the plugin has loaded, typically during or after
     * {@code onEnable()}.</p>
     */
    public static EffectDataType INSTANCE;

    /**
     * Protected constructor to enforce singleton pattern.
     */
    protected EffectDataType() {}

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<Effect> getComplexType() {
        return Effect.class;
    }
}
