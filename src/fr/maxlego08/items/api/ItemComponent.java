package fr.maxlego08.items.api;

import org.bukkit.block.sign.SignSide;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface ItemComponent {

    void setItemName(ItemMeta itemMeta, String name);

    void setDisplayName(ItemMeta itemMeta, String name);

    void setLore(ItemMeta itemMeta, List<String> lore);

    void setLine(SignSide signSide, int index, String line);
}
