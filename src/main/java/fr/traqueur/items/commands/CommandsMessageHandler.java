package fr.traqueur.items.commands;

import fr.traqueur.commands.api.logging.MessageHandler;
import fr.traqueur.items.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandsMessageHandler implements MessageHandler {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    @Override
    public String getNoPermissionMessage() {
        return serialize(Messages.NO_PERMISSION.get());
    }

    @Override
    public String getOnlyInGameMessage() {
        return serialize(Messages.ONLY_IN_GAME.get());
    }

    @Override
    public String getArgNotRecognized() {
        return serialize(Messages.ARG_NOT_RECOGNIZED.get());
    }

    @Override
    public String getRequirementMessage() {
        return serialize(Messages.REQUIREMENT_NOT_MET.get());
    }

    @Override
    public String getCommandDisabledMessage() {
        return serialize(Messages.COMMAND_DISABLED.get());
    }

    @Override
    public String getArgumentTooLongMessage() {
        return serialize(Messages.ARGUMENT_TOO_LONG.get());
    }

    @Override
    public String getInvalidFormatMessage() {
        return serialize(Messages.INVALID_FORMAT.get());
    }

    private String serialize(String message) {
        return SERIALIZER.serialize(MINI_MESSAGE.deserialize(message));
    }

}
