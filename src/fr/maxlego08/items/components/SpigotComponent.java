package fr.maxlego08.items.components;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.zcore.utils.ZUtils;
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
}
