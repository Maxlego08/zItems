package fr.traqueur.items;

import fr.traqueur.items.api.placeholders.PlaceholderParser;
import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Messages implements Loadable {

    NO_PERMISSION("<red>You do not have permission to execute this command."),
    ONLY_IN_GAME("<red>This command can only be executed in-game."),
    ARG_NOT_RECOGNIZED("<red>Argument not recognized."),
    REQUIREMENT_NOT_MET("<red>You do not meet the requirements to perform this command."),
    COMMAND_DISABLED("<red>This command is currently disabled."),
    ARGUMENT_TOO_LONG("<red>Too many arguments provided."),
    INVALID_FORMAT("<red>Invalid command format."),

    EFFECT_APPLIED("<green>Effect <yellow><effect></yellow> applied successfully."),
    EFFECT_ALREADY_PRESENT("<red>Effect <yellow><effect></yellow> is already present on this item."),
    EFFECT_INCOMPATIBLE("<red>Effect <yellow><effect></yellow> is incompatible with existing effects on this item."),
    EFFECT_NOT_ALLOWED("<red>Additional effects are not allowed on this item."),
    EFFECT_DISABLED("<red>Effect <yellow><effect></yellow> is disabled for this item."),
    EFFECT_HANDLER_NOT_FOUND("<red>Handler not found for effect <yellow><effect></yellow>."),

    ITEM_GIVEN("<green>Given <yellow><amount>x <item></yellow> to <aqua><player></aqua>."),
    ITEM_RECEIVED("<green>You received <yellow><amount>x <item></yellow>."),
    ITEM_GIVE_INVALID_AMOUNT("<red>Invalid amount! Amount must be greater than 0."),

    EFFECT_GIVEN("<green>Given <yellow><amount>x <effect></yellow> to <aqua><player></aqua>."),
    EFFECT_RECEIVED("<green>You received <yellow><amount>x <effect></yellow>."),
    EFFECT_GIVE_INVALID_AMOUNT("<red>Invalid amount! Amount must be greater than 0."),
    EFFECT_NO_REPRESENTATION("<red>Effect <yellow><effect></yellow> has no representation and cannot be given as an item."),

    EFFECTS_LORE_HEADER(""),
    EFFECTS_LORE_TITLE("<gray>Effects"),
    EFFECTS_LORE_LINE("<dark_gray>- <effect>"),
    EFFECTS_LORE_MORE("<dark_gray>- <white>And More..."),

    VIEW_NO_ITEM("<red>You must be holding an item!"),
    VIEW_NO_EFFECTS("<yellow>This item has no effects."),
    VIEW_HEADER("<gray>======== <aqua>Item Effects (<count>) <gray>========"),
    VIEW_EFFECT_LINE("<dark_gray>• <display-name> <dark_gray>(<type>) <gray>- Priority: <priority>"),
    VIEW_FOOTER("<gray>==============================="),

    FAILED_TO_OPEN_GUI("<red>Failed to open the GUI. Please try again later.")
    ;
    private final String rawMessage;

    Messages(String message) {
        this.rawMessage = message;
    }

    /**
     * Sends this message to a command sender with placeholders replaced.
     * <p>
     * Example usage:
     * <pre>{@code
     * Messages.EFFECT_APPLIED.send(
     *     player,
     *     Placeholder.parsed("effect", "super_hammer")
     * )
     * }</pre>
     *
     * @param sender       the command sender
     * @param placeholders the placeholders to replace
     */
    public void send(CommandSender sender, TagResolver... placeholders) {
        MessageUtil.sendMessage(sender, this.rawMessage, placeholders);
    }

    public String get() {
        return this.rawMessage;
    }
}
