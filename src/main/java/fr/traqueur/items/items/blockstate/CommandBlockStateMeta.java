package fr.traqueur.items.items.blockstate;

import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.structura.annotations.Options;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;

/**
 * BlockState configuration for command blocks.
 * Allows setting command and name.
 */
@AutoBlockStateMeta("command-block")
public record CommandBlockStateMeta(
        @Options(optional = true) String command,
        @Options(optional = true) Component name
) implements BlockStateMeta<CommandBlock> {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();

    @Override
    public void apply(Player player, CommandBlock commandBlock) {
        if (command != null && !command.isEmpty()) {
            commandBlock.setCommand(command);
        }

        if (name != null) {
            if (PlatformType.isPaper()) {
                commandBlock.name(name);
            } else {
                commandBlock.setName(LEGACY_SERIALIZER.serialize(name));
            }
        }
    }
}