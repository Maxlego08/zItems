package fr.maxlego08.items.components;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.utils.ZUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperComponent extends ZUtils implements ItemComponent {

    private final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();
    private final Map<String, String> COLORS_MAPPINGS = new HashMap<>();

    public PaperComponent() {
        this.COLORS_MAPPINGS.put("0", "black");
        this.COLORS_MAPPINGS.put("1", "dark_blue");
        this.COLORS_MAPPINGS.put("2", "dark_green");
        this.COLORS_MAPPINGS.put("3", "dark_aqua");
        this.COLORS_MAPPINGS.put("4", "dark_red");
        this.COLORS_MAPPINGS.put("5", "dark_purple");
        this.COLORS_MAPPINGS.put("6", "gold");
        this.COLORS_MAPPINGS.put("7", "gray");
        this.COLORS_MAPPINGS.put("8", "dark_gray");
        this.COLORS_MAPPINGS.put("9", "blue");
        this.COLORS_MAPPINGS.put("a", "green");
        this.COLORS_MAPPINGS.put("b", "aqua");
        this.COLORS_MAPPINGS.put("c", "red");
        this.COLORS_MAPPINGS.put("d", "light_purple");
        this.COLORS_MAPPINGS.put("e", "yellow");
        this.COLORS_MAPPINGS.put("f", "white");
        this.COLORS_MAPPINGS.put("k", "obfuscated");
        this.COLORS_MAPPINGS.put("l", "bold");
        this.COLORS_MAPPINGS.put("m", "strikethrough");
        this.COLORS_MAPPINGS.put("n", "underlined");
        this.COLORS_MAPPINGS.put("o", "italic");
        this.COLORS_MAPPINGS.put("r", "reset");
    }

    private String colorMiniMessage(String message) {
        StringBuilder stringBuilder = new StringBuilder();

        Pattern pattern = Pattern.compile("(?<!<)(?<!:)#([a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            matcher.appendReplacement(stringBuilder, "<$0>");
        }
        matcher.appendTail(stringBuilder);

        String newMessage = stringBuilder.toString();

        for (Map.Entry<String, String> entry : this.COLORS_MAPPINGS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            newMessage = newMessage.replace("&" + key, "<" + value + ">");
            newMessage = newMessage.replace("§" + key, "<" + value + ">");
            newMessage = newMessage.replace("&" + key.toUpperCase(), "<" + value + ">");
            newMessage = newMessage.replace("§" + key.toUpperCase(), "<" + value + ">");
        }

        return newMessage;
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
        message(player, Message.COMMAND_ITEM_LORE);
        if (lore != null) {
            for (Component component : lore) {
                Component message = getComponent(Message.COMMAND_ITEM_LORE_LINE.getMessage());
                player.sendMessage(message.append(component));
            }
        }
    }
}
