package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.annotations.SinceVersion;
import fr.traqueur.items.api.items.BlockDataMeta;
import org.bukkit.block.Orientation;
import org.bukkit.block.data.type.Jigsaw;

/**
 * Paper implementation of jigsaw BlockData metadata.
 * Uses the Paper-specific {@link Orientation} API.
 */
@AutoBlockDataMeta("jigsaw")
@PaperOnly
@SinceVersion("1.21.5")
public record JigsawMetaPaper(Orientation orientation) implements BlockDataMeta<Jigsaw> {

    @Override
    public void apply(Jigsaw blockData) {
        blockData.setOrientation(orientation);
    }
}
