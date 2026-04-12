package fr.traqueur.items.api.services;

import org.bukkit.inventory.ItemStack;

/**
 * Service for hiding the attribute tooltip section on an item.
 * <p>
 * The default implementation uses {@code ItemFlag.HIDE_ATTRIBUTES} (Bukkit/Paper &lt; 1.21.5).
 * On Paper 1.21.5+, a version-specific implementation in the {@code versions/paper-v1_21_5} module
 * overrides this via the {@link java.util.ServiceLoader} mechanism, using the
 * {@code TOOLTIP_DISPLAY} data component API.
 * <p>
 * Loaded once via {@code ServiceLoader.load(AttributeTooltipService.class)} and cached statically.
 */
public interface AttributeTooltipService {

    /**
     * Hides the attribute modifiers section in the item's tooltip.
     *
     * @param itemStack the item to modify
     */
    void hideAttributesTooltip(ItemStack itemStack);
}
