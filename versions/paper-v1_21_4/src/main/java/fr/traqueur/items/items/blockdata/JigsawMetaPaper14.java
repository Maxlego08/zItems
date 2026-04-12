package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.UntilVersion;
import fr.traqueur.items.api.items.BlockDataMeta;
import org.bukkit.block.data.type.Jigsaw;

/**
 * Paper implementation of jigsaw BlockData metadata for Paper &lt;= 1.21.4.
 * Uses the Bukkit-nested {@link Jigsaw.Orientation} enum, which is available on Paper 1.21.4
 * but was superseded by the top-level {@code org.bukkit.block.Orientation} in Paper 1.21.5+.
 */
@AutoBlockDataMeta("jigsaw")
@PaperOnly
@UntilVersion("1.21.4")
public record JigsawMetaPaper14(Jigsaw.Orientation orientation) implements BlockDataMeta<Jigsaw> {

    @Override
    public void apply(Jigsaw blockData) {
        blockData.setOrientation(orientation);
    }
}