package fr.traqueur.items.items.blockstate;

import fr.traqueur.items.api.services.BlockComponentService;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.annotations.AutoBlockStateMeta;
import fr.traqueur.items.api.items.BlockStateMeta;
import fr.traqueur.items.paper.PaperBlockComponentService;
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
    private static final BlockComponentService BLOCK_COMPONENT_SERVICE =
            PlatformType.isPaper() ? new PaperBlockComponentService() : null;

    @Override
    public void apply(Player player, CommandBlock commandBlock) {
        if (command != null && !command.isEmpty()) {
            commandBlock.setCommand(command);
        }

        if (name != null) {
            if (BLOCK_COMPONENT_SERVICE != null) {
                BLOCK_COMPONENT_SERVICE.setName(commandBlock, name);
            } else {
                commandBlock.setName(LEGACY_SERIALIZER.serialize(name));
            }
        }
    }
}