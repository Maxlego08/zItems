package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * BlockState metadata configuration for items.
 * Allows configuring BlockState properties for block items that have tile entities.
 * This is different from BlockDataMetadata which handles BlockData (block properties).
 *
 * BlockState handles tile entity data like:
 * - Container contents (chests, barrels, shulker boxes)
 * - Sign text and formatting
 * - Spawner settings
 * - Beacon effects
 * - Command block commands
 * - etc.
 *
 * Discriminator key: "block-state"
 */
@AutoMetadata("block-state")
public record BlockStateMetadata(
        @Options(optional = true) List<BlockStateMeta<?>> settings
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        // Edit the BlockStateMeta of the item
        boolean applied = ItemUtil.editMeta(itemStack, org.bukkit.inventory.meta.BlockStateMeta.class, meta -> {
            BlockState blockState = meta.getBlockState();

            // Apply all settings to the BlockState
            if (settings != null && !settings.isEmpty()) {
                for (BlockStateMeta<?> stateMeta : settings) {
                    applyMeta(player, blockState, stateMeta);
                }
            }

            // Update the BlockState back to the meta
            meta.setBlockState(blockState);
        });

        if (!applied) {
            Logger.severe("Failed to apply BlockState to ItemStack of type {}", itemStack.getType().name());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BlockState> void applyMeta(Player player, BlockState blockState, BlockStateMeta<T> meta) {
        try {
            meta.apply(player, (T) blockState);
        } catch (ClassCastException e) {
            Logger.severe("Failed to apply BlockStateMeta: type mismatch for {} - expected compatible with {}",
                    e, blockState.getClass().getSimpleName(), meta.getClass().getSimpleName());
        }
    }
}