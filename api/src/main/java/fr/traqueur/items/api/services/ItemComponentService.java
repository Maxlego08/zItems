package fr.traqueur.items.api.services;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Service for applying Adventure Component data to ItemMeta using Paper's native API.
 * <p>
 * Loaded via {@link java.util.ServiceLoader} — the {@code versions/paper} module provides
 * the implementation. When absent (Spigot), {@link fr.traqueur.items.api.utils.ItemUtil}
 * falls back to legacy string serialization.
 */
public interface ItemComponentService {

    void setDisplayName(ItemMeta meta, Component displayName);

    void setLore(ItemMeta meta, List<Component> lore);

    List<Component> getLore(ItemMeta meta);

    void setItemName(ItemMeta meta, Component itemName);
}
