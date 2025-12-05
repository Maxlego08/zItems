package fr.traqueur.items.items.blockdata;

import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockDataMeta;
import fr.traqueur.items.api.items.BlockDataMeta;
import fr.traqueur.structura.annotations.Options;
import org.bukkit.block.Orientation;
import org.bukkit.block.data.type.Crafter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * BlockData metadata for crafter blocks.
 * Sets crafting state, triggered state, and orientation.
 */
@AutoBlockDataMeta("crafter")
public record CrafterMeta(
        boolean crafting,
        boolean triggered,
        @Options(optional = true) Crafter.Orientation orientation
) implements BlockDataMeta<Crafter> {

    @Override
    public void apply(Crafter blockData) {
        blockData.setCrafting(crafting);
        blockData.setTriggered(triggered);
        if (orientation != null) {
            if (PlatformType.isPaper()) {
                Orientation o = Orientation.valueOf(orientation.name());
                blockData.setOrientation(o);
            } else {
                try {
                    Method method = blockData.getClass().getMethod("setOrientation", Crafter.Orientation.class);
                    method.invoke(blockData, orientation);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
