package fr.maxlego08.items.components;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.utils.MessageUtils;
import fr.maxlego08.items.zcore.utils.ZUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperComponent implements ItemComponent {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();
    private static final Map<String, String> COLORS_MAPPINGS = new HashMap<>();

    static {
        COLORS_MAPPINGS.put("0", "black");
        COLORS_MAPPINGS.put("1", "dark_blue");
        COLORS_MAPPINGS.put("2", "dark_green");
        COLORS_MAPPINGS.put("3", "dark_aqua");
        COLORS_MAPPINGS.put("4", "dark_red");
        COLORS_MAPPINGS.put("5", "dark_purple");
        COLORS_MAPPINGS.put("6", "gold");
        COLORS_MAPPINGS.put("7", "gray");
        COLORS_MAPPINGS.put("8", "dark_gray");
        COLORS_MAPPINGS.put("9", "blue");
        COLORS_MAPPINGS.put("a", "green");
        COLORS_MAPPINGS.put("b", "aqua");
        COLORS_MAPPINGS.put("c", "red");
        COLORS_MAPPINGS.put("d", "light_purple");
        COLORS_MAPPINGS.put("e", "yellow");
        COLORS_MAPPINGS.put("f", "white");
        COLORS_MAPPINGS.put("k", "obfuscated");
        COLORS_MAPPINGS.put("l", "bold");
        COLORS_MAPPINGS.put("m", "strikethrough");
        COLORS_MAPPINGS.put("n", "underlined");
        COLORS_MAPPINGS.put("o", "italic");
        COLORS_MAPPINGS.put("r", "reset");
    }

    private String colorMiniMessage(String message) {
        // First, convert legacy color codes to MiniMessage format
        for (Map.Entry<String, String> entry : COLORS_MAPPINGS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            message = message.replace("&" + key, "<" + value + ">");
            message = message.replace("§" + key, "<" + value + ">");
            message = message.replace("&" + key.toUpperCase(), "<" + value + ">");
            message = message.replace("§" + key.toUpperCase(), "<" + value + ">");
        }

        // Then convert hex colors that are NOT already inside MiniMessage tags
        // We parse character by character to avoid converting hex inside existing tags
        StringBuilder result = new StringBuilder();
        int i = 0;
        int length = message.length();

        while (i < length) {
            char currentChar = message.charAt(i);

            // If we encounter a '<', skip the entire tag to avoid modifying it
            if (currentChar == '<') {
                int closeIndex = message.indexOf('>', i);
                if (closeIndex != -1) {
                    // Copy the entire tag as-is (including < and >)
                    result.append(message.substring(i, closeIndex + 1));
                    i = closeIndex + 1;
                    continue;
                }
            }

            // Check if we have a hex color code (#RRGGBB)
            if (currentChar == '#' && i + 6 < length) {
                String potentialHex = message.substring(i + 1, i + 7);
                if (potentialHex.matches("[a-fA-F0-9]{6}")) {
                    // Valid hex color, wrap it in MiniMessage tags
                    result.append("<#").append(potentialHex).append(">");
                    i += 7;
                    continue;
                }
            }

            // Regular character, just append it
            result.append(currentChar);
            i++;
        }

        return result.toString();
    }

    private TextDecoration.State getState(String text) {
        return text.contains("&o") || text.contains("<i>") || text.contains("<em>") || text.contains("<italic>") ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
    }

    @Override
    public void setItemName(ItemMeta itemMeta, String name) {
        var component = name.isEmpty() ? null : getComponent(name).decoration(TextDecoration.ITALIC, getState(name));
        itemMeta.itemName(component);
    }

    @Override
    public void setDisplayName(ItemMeta itemMeta, String name) {
        var component = name.isEmpty() ? null : getComponent(name).decoration(TextDecoration.ITALIC, getState(name));
        itemMeta.displayName(component);
    }

    @Override
    public void setLore(ItemMeta itemMeta, List<String> lore) {
        var components = lore.stream().map(line -> getComponent(line).decoration(TextDecoration.ITALIC, getState(line))).toList();
        itemMeta.lore(components);
    }

    @Override
    public void setLine(SignSide signSide, int index, String line) {
        signSide.line(index, getComponent(line).decoration(TextDecoration.ITALIC, getState(line)));
    }

    @Override
    public void addLoreLine(ItemMeta itemMeta, String line) {

        List<Component> components = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();
        if (components == null) components = new ArrayList<>();

        components.add(getComponent(line).decorationIfAbsent(TextDecoration.ITALIC, getState(line)));
        itemMeta.lore(components);
    }

    public Component getComponent(String message) {
        return this.MINI_MESSAGE.deserialize(colorMiniMessage(message));
    }

    @Override
    public void setLoreIndex(ItemMeta itemMeta, int index, String line) {
        List<Component> components = itemMeta.hasLore() ? itemMeta.lore() : new ArrayList<>();
        if (components == null) return;
        components.set(index - 1, getComponent(line).decorationIfAbsent(TextDecoration.ITALIC, getState(line)));
        itemMeta.lore(components);
    }

    @Override
    public void sendItemLore(Player player, ItemMeta itemMeta) {
        List<Component> lore = itemMeta.lore();


        player.sendMessage(getComponent(MessageUtils.getMessage(Message.COMMAND_ITEM_LORE)));

        if (lore != null) {
            for (Component component : lore) {
                Component message = getComponent(Message.COMMAND_ITEM_LORE_LINE.getMessage());
                player.sendMessage(message.append(component));
            }
        }
    }

    @Override
    public void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(getComponent(string));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        player.sendActionBar(getComponent(message));
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        var titleComponent = getComponent(title);
        var subtitleComponent = getComponent(subtitle);
        player.showTitle(net.kyori.adventure.title.Title.title(
                titleComponent,
                subtitleComponent,
                net.kyori.adventure.title.Title.Times.times(
                        java.time.Duration.ofMillis(fadeInTime),
                        java.time.Duration.ofMillis(showTime),
                        java.time.Duration.ofMillis(fadeOutTime)
                )
        ));
    }

}
