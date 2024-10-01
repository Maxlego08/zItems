package fr.maxlego08.items.components;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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
}
