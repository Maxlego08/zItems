package fr.maxlego08.items.zcore.utils;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.zcore.ZPlugin;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.MessageType;
import fr.maxlego08.items.zcore.utils.nms.NmsVersion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows you to manage messages sent to players and the console.
 * Provides various utility methods for sending and formatting messages.
 * Extends {@link LocationUtils}.
 *
 * @see LocationUtils
 */
public abstract class MessageUtils extends LocationUtils {

    private final static int CENTER_PX = 154;

    /**
     * Sends a message with prefix to the specified command sender.
     *
     * @param sender  the command sender to send the message to.
     * @param message the message to send.
     * @param args    the arguments for the message.
     */
    protected void message(CommandSender sender, String message, Object... args) {
        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        plugin.getItemComponent().sendMessage(sender, Message.PREFIX.msg() + getMessage(message, args));
    }


    /**
     * Sends a chat message to the specified player.
     *
     * @param player  the player to send the message to.
     * @param message the message to send.
     * @param args    the arguments for the message.
     */
    private void sendTchatMessage(Player player, Message message, Object... args) {
        if (message.getMessages().size() > 0) {
            message.getMessages().forEach(msg -> message(player, this.papi(getMessage(msg, args), player)));
        } else {
            message(player, this.papi((message.getType() == MessageType.WITHOUT_PREFIX ? "" : Message.PREFIX.msg()) + getMessage(message, args), player));
        }
    }

    /**
     * Allows you to send a message to a command sender.
     *
     * @param sender  the user who sent the command.
     * @param message the message - using the Message enum for simplified message management.
     * @param args    the arguments - the arguments work in pairs, you must put for example %test% and then the value.
     */
    public void message(CommandSender sender, Message message, Object... args) {
        ItemsPlugin plugin = JavaPlugin.getPlugin(ItemsPlugin.class);
        if (sender instanceof ConsoleCommandSender) {
            if (message.getMessages().size() > 0) {
                message.getMessages().forEach(msg -> message(sender, getMessage(msg, args)));
            } else {
                message(sender, Message.PREFIX.msg() + getMessage(message, args));
            }
        } else {
            Player player = (Player) sender;
            switch (message.getType()) {
                case CENTER:
                    if (message.getMessages().size() > 0) {
                        message.getMessages().forEach(msg -> sender.sendMessage(this.getCenteredMessage(this.papi(getMessage(msg, args), player))));
                    } else {
                        sender.sendMessage(this.getCenteredMessage(this.papi(getMessage(message, args), player)));
                    }
                    break;
                case ACTION:
                    plugin.getItemComponent().sendActionBar(player, getMessage(message, args));
                    break;
                case TCHAT_AND_ACTION:
                    plugin.getItemComponent().sendActionBar(player, getMessage(message, args));
                    sendTchatMessage(player, message, args);
                    break;
                case TCHAT:
                case WITHOUT_PREFIX:
                    sendTchatMessage(player, message, args);
                    break;
                case TITLE:
                    String title = message.getTitle();
                    String subTitle = message.getSubTitle();
                    int fadeInTime = message.getStart();
                    int showTime = message.getTime();
                    int fadeOutTime = message.getEnd();
                    plugin.getItemComponent().sendTitle(player, this.papi(this.getMessage(title, args), player), this.papi(this.getMessage(subTitle, args), player), fadeInTime, showTime, fadeOutTime);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Gets the formatted message with arguments replaced.
     *
     * @param message the message to format.
     * @param args    the arguments for the message.
     * @return the formatted message.
     */
    public static String getMessage(Message message, Object... args) {
        return getMessage(message.getMessage(), args);
    }

    /**
     * Gets the formatted message with arguments replaced.
     *
     * @param message the message to format.
     * @param args    the arguments for the message.
     * @return the formatted message.
     */
    public static String getMessage(String message, Object... args) {
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Number of invalid arguments. Arguments must be in pairs.");
        }

        for (int i = 0; i < args.length; i += 2) {
            if (args[i] == null || args[i + 1] == null) {
                throw new IllegalArgumentException("Keys and replacement values must not be null.");
            }
            message = message.replace(args[i].toString(), args[i + 1].toString());
        }
        return message;
    }

    /**
     * Gets a centered message.
     *
     * @param message the message to center.
     * @return the centered message.
     */
    protected String getCenteredMessage(String message) {
        if (message == null || message.equals("")) {
            return "";
        }
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

}
