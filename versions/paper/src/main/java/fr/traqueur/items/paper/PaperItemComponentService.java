package fr.traqueur.items.paper;

import fr.traqueur.items.api.services.ItemComponentService;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Paper implementation of {@link ItemComponentService} using Paper's native Adventure API.
 * Registered via {@code META-INF/services/} and discovered by {@link java.util.ServiceLoader}.
 */
public class PaperItemComponentService implements ItemComponentService {

    @Override
    public void setDisplayName(ItemMeta meta, Component displayName) {
        meta.displayName(displayName);
    }

    @Override
    public void setLore(ItemMeta meta, List<Component> lore) {
        meta.lore(lore);
    }

    @Override
    public List<Component> getLore(ItemMeta meta) {
        return meta.lore();
    }

    @Override
    public void setItemName(ItemMeta meta, Component itemName) {
        meta.itemName(itemName);
    }
}