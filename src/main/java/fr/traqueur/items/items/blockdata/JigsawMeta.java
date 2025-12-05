package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.items.BlockDataMeta;
import org.bukkit.block.Orientation;
import org.bukkit.block.data.type.Crafter;
import org.bukkit.block.data.type.Jigsaw;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BlockData metadata for jigsaw blocks.
 * Sets the orientation.
 */
@AutoBlockDataMeta("jigsaw")
public record JigsawMeta(Jigsaw.Orientation orientation) implements BlockDataMeta<Jigsaw> {

    @Override
    public void apply(Jigsaw blockData) {
        if (PlatformType.isPaper()) {
            Orientation o = Orientation.valueOf(orientation.name());
            blockData.setOrientation(o);
        } else {
            try {
                Method method = blockData.getClass().getMethod("setOrientation", Jigsaw.Orientation.class);
                method.invoke(blockData, orientation);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
