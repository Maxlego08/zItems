package fr.traqueur.items.serialization;

import fr.traqueur.items.api.blocks.TrackedBlock;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Custom {@link PersistentDataType} for serializing {@link TrackedBlock} objects to/from chunk PDC.
 *
 * <p>This class enables storing block tracking data in chunk metadata using Bukkit's
 * {@link PersistentDataContainer} system. It allows the plugin to remember which blocks
 * were placed as custom items, ensuring correct drops when broken.</p>
 *
 * <h2>Singleton Pattern</h2>
 * <p>This class follows the singleton pattern with a static {@link #INSTANCE} field
 * initialized by the plugin implementation during startup.</p>
 *
 * <h2>Implementation Requirements</h2>
 * <p>Concrete implementations must:</p>
 * <ol>
 *   <li>Implement methods to serialize TrackedBlock to PersistentDataContainer</li>
 *   <li>Initialize the {@link #INSTANCE} field during plugin startup</li>
 *   <li>Handle position packing/unpacking correctly</li>
 * </ol>
 *
 * @see TrackedBlock
 * @see EffectDataType
 * @see org.bukkit.persistence.PersistentDataType
 */
public abstract class TrackedBlockDataType implements PersistentDataType<PersistentDataContainer, TrackedBlock> {

    /**
     * Singleton instance of the TrackedBlockDataType implementation.
     *
     * <p>This field is initialized by the plugin implementation during startup.
     * It must be accessed after the plugin has loaded.</p>
     */
    public static TrackedBlockDataType INSTANCE;

    /**
     * Protected constructor to enforce singleton pattern.
     */
    protected TrackedBlockDataType() {
    }

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<TrackedBlock> getComplexType() {
        return TrackedBlock.class;
    }
}
