package fr.maxlego08.items.api;

import fr.maxlego08.items.api.configurations.ItemConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents an item which can be used in the game.
 *
 * @author Maxime LEGER
 */
public interface Item {

    /**
     * The key used to store the item id in an item stack.
     */
    NamespacedKey ITEM_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(ItemPlugin.class), "item-id");

    /**
     * Get the item configuration.
     *
     * @return the item configuration
     */
    ItemConfiguration getConfiguration();

    /**
     * Get the item name.
     *
     * @return the item name
     */
    String getName();

    /**
     * Build an item stack with the given amount.
     *
     * @param player the player who will receive the item stack
     * @param amount the amount of the item stack
     * @return the item stack
     */
    ItemStack build(Player player, int amount);
}
