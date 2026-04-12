package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.UntilVersion;
import fr.traqueur.items.api.items.BlockDataMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.block.data.type.Crafter;

/**
 * Paper implementation of crafter BlockData metadata for Paper &lt;= 1.21.4.
 * Uses the Bukkit-nested {@link Crafter.Orientation} enum, which is available on Paper 1.21.4
 * but was superseded by the top-level {@code org.bukkit.block.Orientation} in Paper 1.21.5+.
 */
@AutoBlockDataMeta("crafter")
@PaperOnly
@UntilVersion("1.21.4")
public record CrafterMetaPaper14(
        boolean crafting,
        boolean triggered,
        @Options(optional = true) Crafter.Orientation orientation
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