package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.BlockDataMeta;
import org.bukkit.block.data.type.Jigsaw;

/**
 * Spigot implementation of jigsaw BlockData metadata.
 * Uses {@link Jigsaw.Orientation} directly (Bukkit API), avoiding Paper-specific classes.
 */
@AutoBlockDataMeta("jigsaw")
@SpigotOnly
public record JigsawMetaSpigot(Jigsaw.Orientation orientation) implements BlockDataMeta<Jigsaw> {

    @Override
    public void apply(Jigsaw blockData) {
        blockData.setOrientation(orientation);
    }
}
