package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.BlockDataMeta;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * BlockData metadata configuration for items.
 * Allows configuring BlockData properties for block items.
 * Discriminator key: "block-data"
 */
@AutoMetadata("block-data")
public record BlockDataMetadata(
        @Options(optional = true) List<BlockDataMeta<?>> settings
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {

        // Create BlockData using Bukkit's standard method
        BlockData blockData = Bukkit.createBlockData(itemStack.getType());

        // Apply all settings to the BlockData
        if (settings != null && !settings.isEmpty()) {
            for (BlockDataMeta<?> meta : settings) {
                applyMeta(blockData, meta);
            }
        }

        // Apply BlockData to ItemStack using Spigot's standard API
        boolean apply = ItemUtil.editMeta(itemStack, org.bukkit.inventory.meta.BlockDataMeta.class, meta -> {
            meta.setBlockData(blockData);
        });

        if(!apply){
            Logger.severe("Failed to apply BlockData to ItemStack of type {}", itemStack.getType().name());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends BlockData> void applyMeta(BlockData blockData, BlockDataMeta<T> meta) {
        try {
            meta.apply((T) blockData);
        } catch (ClassCastException e) {
            Logger.severe("Failed to apply BlockDataMeta type mismatch for {}", e, blockData.getClass().getSimpleName());
        }
    }
}
