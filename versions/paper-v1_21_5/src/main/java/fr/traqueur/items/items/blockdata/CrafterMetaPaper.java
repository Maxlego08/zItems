package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.items.BlockDataMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.block.Orientation;
import org.bukkit.block.data.type.Crafter;

/**
 * Paper implementation of crafter BlockData metadata.
 * Uses the Paper-specific {@link Orientation} API for setting the crafter's orientation.
 * Compiled against paper-api:1.21.5+ in the versions/paper-v1_21_5 module.
 */
@AutoBlockDataMeta("crafter")
@PaperOnly
public record CrafterMetaPaper(
        boolean crafting,
        boolean triggered,
        @Options(optional = true) Orientation orientation
) implements BlockDataMeta<Crafter> {

    @Override
    public void apply(Crafter blockData) {
        blockData.setCrafting(crafting);
        blockData.setTriggered(triggered);
        if (orientation != null) {
           blockData.setOrientation(orientation);
        }
    }
}
