package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.BlockDataMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.block.data.type.Crafter;

/**
 * Spigot implementation of crafter BlockData metadata.
 * Uses the Bukkit {@link Crafter.Orientation} API directly, without Paper-specific classes.
 */
@AutoBlockDataMeta("crafter")
@SpigotOnly
public record CrafterMetaSpigot(
        boolean crafting,
        boolean triggered,
        @Options(optional = true) Crafter.Orientation orientation
) implements BlockDataMeta<Crafter> {

    @Override
    public void apply(Crafter blockData) {
        blockData.setCrafting(crafting);
        blockData.setTriggered(triggered);
        if (orientation != null) {
            // Spigot uses Crafter.Orientation directly
            blockData.setOrientation(orientation);
        }
    }
}
