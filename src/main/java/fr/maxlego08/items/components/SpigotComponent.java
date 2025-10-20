package fr.maxlego08.items.components;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.zcore.utils.ZUtils;
import fr.maxlego08.items.zcore.utils.nms.NmsVersion;
import org.bukkit.block.sign.SignSide;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpigotComponent extends ZUtils implements ItemComponent {

    @Override
    public void setItemName(ItemMeta itemMeta, String name) {
        itemMeta.setItemName(color(name));
    }

    @Override
    public void setDisplayName(ItemMeta itemMeta, String name) {
        itemMeta.setDisplayName(color(name));
    }

    @Override
    public void setLore(ItemMeta itemMeta, List<String> lore) {
        itemMeta.setLore(color(lore));
    }

    @Override
    public void setLine(SignSide signSide, int index, String line) {
        signSide.setLine(index, color(line));
    }

    @Override
    public void addLoreLine(ItemMeta itemMeta, String line) {
    }

    @Override
    public void setLoreIndex(ItemMeta itemMeta, int index, String loreLine) {
    }

    @Override
    public void sendItemLore(Player player, ItemMeta itemMeta) {
    }

    @Override
    public void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(color(string));
    }

    @Override
    public void sendActionBar(Player player, String message) {
        player.sendActionBar(color(message));
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        player.sendTitle(color(title), color(subtitle), fadeInTime, showTime, fadeOutTime);
    }

    private List<String> color(List<String> messages) {
        return messages.stream().map(this::color).toList();
    }

    private String color(String message) {
        if (message == null) {
            return null;
        }
        if (NmsVersion.nmsVersion.isHexVersion()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color)));
                matcher = pattern.matcher(message);
            }
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }
}
