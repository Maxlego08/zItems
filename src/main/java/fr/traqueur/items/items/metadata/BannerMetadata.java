package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.items.settings.models.PatternWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoMetadata("banner")
public record BannerMetadata(List<PatternWrapper> patterns) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        boolean applied = ItemUtil.editMeta(itemStack, BannerMeta.class, meta -> {
            meta.setPatterns(patterns.stream().map(PatternWrapper::toPattern).toList());
        });
        if (!applied) {
            Logger.severe("Failed to apply BannerMeta to ItemStack of type {}", itemStack.getType().name());
        }
    }



}
