package fr.traqueur.items.items.metadata;

import com.destroystokyo.paper.inventory.meta.ArmorStandMeta;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@AutoMetadata("armor-stand")
@AutoMetadata.PaperMetadata
public record ArmorStandMetadata(
        @Options(optional = true) @DefaultBool(false) boolean invisible,
        @Options(optional = true) @DefaultBool(false) boolean small,
        @Options(optional = true) @DefaultBool(false) boolean arms,
        @Options(optional = true) @DefaultBool(false) boolean noBasePlate,
        @Options(optional = true) @DefaultBool(false) boolean marker
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        boolean applied = itemStack.editMeta(ArmorStandMeta.class, armorStandMeta -> {
            armorStandMeta.setInvisible(invisible);
            armorStandMeta.setSmall(small);
            armorStandMeta.setNoBasePlate(noBasePlate);
            armorStandMeta.setShowArms(arms);
            armorStandMeta.setMarker(marker);
        });
        if (!applied) {
            Logger.severe("Failed to apply ArmorStandMeta to ItemStack of type {}", itemStack.getType().name());
        }
    }
}
