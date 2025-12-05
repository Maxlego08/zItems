package fr.traqueur.items.items.blockstate;

import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.DyeColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * BlockState configuration for sign blocks.
 * Allows setting text, color, and glow on both sides of the sign.
 */
@AutoBlockStateMeta("sign")
public record SignStateMeta(
        @Options(optional = true) @DefaultBool(false) boolean waxed,
        @Options(optional = true) SignSideConfig front,
        @Options(optional = true) SignSideConfig back
) implements BlockStateMeta<Sign> {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    @Override
    public void apply(Player player, Sign sign) {
        sign.setWaxed(waxed);

        if (front != null) {
            applySide(sign.getSide(Side.FRONT), front);
        }

        if (back != null) {
            applySide(sign.getSide(Side.BACK), back);
        }
    }

    private void applySide(SignSide signSide, SignSideConfig config) {
        if (config.glow()) {
            signSide.setGlowingText(true);
        }

        if (config.color() != null) {
            signSide.setColor(config.color());
        }

        if (config.lines() != null) {
            for (int i = 0; i < Math.min(config.lines().size(), 4); i++) {
                Component line = config.lines().get(i);
                if (line != null) {
                    if(PlatformType.isPaper()) {
                        signSide.line(i, line);
                    } else {
                        signSide.setLine(i, LEGACY_SERIALIZER.serialize(line));
                    }
                }
            }
        }
    }

    public record SignSideConfig(
            @Options(optional = true) @DefaultBool(false) boolean glow,
            @Options(optional = true) DyeColor color,
            @Options(optional = true) List<Component> lines
    ) implements Loadable {
    }
}