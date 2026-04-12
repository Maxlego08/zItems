package fr.traqueur.items.serialization;

import fr.traqueur.items.api.blocks.TrackedBlock;
import fr.traqueur.items.blocks.ZTrackedBlock;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

/**
 * PersistentDataType for serializing TrackedBlock objects.
 * <p>
 * Converts TrackedBlock to/from PersistentDataContainer for storage.
 * Uses nested PDC with Keys.TRACKED_BLOCK_POSITION and Keys.TRACKED_BLOCK_ITEM_ID
 */
public class ZTrackedBlockDataType extends TrackedBlockDataType {

    public static void initialize() {
        TrackedBlockDataType.INSTANCE = new ZTrackedBlockDataType();
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull TrackedBlock complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        Keys.TRACKED_BLOCK_POSITION.set(container, complex.packedPosition());
        Keys.TRACKED_BLOCK_ITEM_ID.set(container, complex.itemId());

        return container;
    }

    @Override
    public @NotNull ZTrackedBlock fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        Integer position = Keys.TRACKED_BLOCK_POSITION.get(primitive).orElseThrow();
        String itemId = Keys.TRACKED_BLOCK_ITEM_ID.get(primitive).orElseThrow();

        return new ZTrackedBlock(position, itemId);
    }
}