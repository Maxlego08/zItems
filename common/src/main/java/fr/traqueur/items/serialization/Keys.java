package fr.traqueur.items.serialization;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.blocks.TrackedBlock;
import fr.traqueur.items.api.effects.Effect;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of all data keys used in the zItems plugin.
 * Each key automatically uses its field name as the key identifier.
 */
public class Keys {

    public static final DataKey<String> ITEM_ID = new DataKey<>(PersistentDataType.STRING);
    public static final DataKey<List<Effect>> EFFECTS = new DataKey<>(PersistentDataType.LIST.listTypeFrom(EffectDataType.INSTANCE));
    public static final DataKey<String> CUSTOM_BLOCK_ID = new DataKey<>(PersistentDataType.STRING);
    public static final DataKey<String> EFFECT_REPRESENTATION = new DataKey<>(PersistentDataType.STRING);

    // Tracked block nested keys (for TrackedBlockDataType serialization)
    public static final DataKey<Integer> TRACKED_BLOCK_POSITION = new DataKey<>(PersistentDataType.INTEGER);
    public static final DataKey<String> TRACKED_BLOCK_ITEM_ID = new DataKey<>(PersistentDataType.STRING);

    // Chunk-level tracked blocks list
    public static final DataKey<List<TrackedBlock>> TRACKED_BLOCKS = new DataKey<>(PersistentDataType.LIST.listTypeFrom(TrackedBlockDataType.INSTANCE));

    // Durability system
    public static final DataKey<String> DURABILITY_MODE = new DataKey<>(PersistentDataType.STRING);
    public static final DataKey<Integer> CUSTOM_DURABILITY = new DataKey<>(PersistentDataType.INTEGER);
    public static final DataKey<Integer> CUSTOM_MAX_DURABILITY = new DataKey<>(PersistentDataType.INTEGER);

    private static ItemsPlugin PLUGIN;

    private Keys() {
    }

    /**
     * Initializes the Keys registry with the given plugin instance.
     * This must be called before using any DataKey.
     *
     * @param plugin the ItemsPlugin instance
     */
    public static void initialize(ItemsPlugin plugin) {
        PLUGIN = plugin;
    }

    /**
     * Generic typed persistent data key that automatically resolves its name from the static field name.
     *
     * @param <T> the type of data this key stores
     */
    public static class DataKey<T> {

        private static final Map<DataKey<?>, String> KEY_NAMES = new HashMap<>();

        private final PersistentDataType<?, T> type;
        private NamespacedKey namespacedKey;

        public DataKey(PersistentDataType<?, T> type) {
            this.type = type;
        }

        public NamespacedKey getNamespacedKey() {
            if (namespacedKey == null) {
                String keyName = resolveFieldName();
                namespacedKey = new NamespacedKey(PLUGIN, keyName.toLowerCase());
            }
            return namespacedKey;
        }

        private String resolveFieldName() {
            String cachedName = KEY_NAMES.get(this);
            if (cachedName != null) {
                return cachedName;
            }

            try {
                Field[] fields = Keys.class.getDeclaredFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers()) &&
                            Modifier.isFinal(field.getModifiers()) &&
                            DataKey.class.isAssignableFrom(field.getType())) {

                        field.setAccessible(true);
                        Object fieldValue = field.get(null);

                        if (fieldValue == this) {
                            String fieldName = field.getName();
                            KEY_NAMES.put(this, fieldName);
                            return fieldName;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to resolve field name for DataKey", e);
            }

            throw new RuntimeException("Could not resolve field name for DataKey instance");
        }

        public Optional<T> get(PersistentDataContainer container) {
            return Optional.ofNullable(container.get(getNamespacedKey(), type));
        }

        public T get(PersistentDataContainer container, T defaultValue) {
            return container.getOrDefault(getNamespacedKey(), type, defaultValue);
        }

        public void set(PersistentDataContainer container, T value) {
            container.set(getNamespacedKey(), type, value);
        }
    }
}