package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Leather armor metadata configuration for dyeable armor.
 * Discriminator key: "leather-armor"
 */
@AutoMetadata("leather-armor")
public record LeatherArmorMetadata(
        Color color
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        boolean applied = ItemUtil.editMeta(itemStack, LeatherArmorMeta.class, meta -> {
            meta.setColor(color);
        });
        if (!applied) {
            Logger.severe("Failed to apply LeatherArmorMeta to ItemStack of type {}", itemStack.getType().name());
        }
    }
}