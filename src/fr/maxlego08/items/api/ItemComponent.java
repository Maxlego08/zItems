package fr.maxlego08.items.api;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public interface ItemComponent {

    void setItemName(ItemMeta itemMeta, String name);

    void setDisplayName(ItemMeta itemMeta, String name);

    void setLore(ItemMeta itemMeta, List<String> lore);

}
