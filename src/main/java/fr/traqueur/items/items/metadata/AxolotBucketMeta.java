package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("axolot-bucket")
public record AxolotBucketMeta(Axolotl.Variant variant) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        boolean applied = ItemUtil.editMeta(itemStack, AxolotlBucketMeta.class, axolotlBucketMeta -> {
            axolotlBucketMeta.setVariant(variant);
        });
        if (!applied) {
            Logger.severe("Failed to apply AxolotlBucketMeta to ItemStack of type {}", itemStack.getType().name());
        }
    }
}
