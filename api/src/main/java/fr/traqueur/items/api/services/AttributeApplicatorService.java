package fr.traqueur.items.api.services;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.settings.models.AttributeWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Service for applying attribute modifiers to items using the Paper DataComponent API.
 * <p>
 * Loaded once via {@link java.util.ServiceLoader} — the {@code versions/paper} module provides
 * the implementation using {@code io.papermc.paper.datacomponent.*}.
 * When absent (Spigot or Paper &lt; 1.21.4), {@code AttributeUtil} falls back to the legacy
 * ItemMeta-based approach.
 */
public interface AttributeApplicatorService {

    /**
     * Applies attributes to the item using the Paper DataComponent API.
     */
    void applyAttributes(ItemStack itemStack, List<AttributeWrapper> attributes,
                         ItemsPlugin plugin, AttributeMergeStrategy strategy);
}
