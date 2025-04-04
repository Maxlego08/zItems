package fr.maxlego08.items.api;

import fr.maxlego08.items.api.utils.ItemFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Represents the manager of the items.
 *
 * @author Maxime LEGER
 */
public interface ItemManager {

    /**
     * Load all the items from the configuration files.
     */
    void loadItems();

    /**
     * Load all the crafts from the configuration files.
     */
    void loadCrafts();

    /**
     * Load an item from a configuration files.
     *
     * @param file the files to load
     */
    void loadItem(File file);

    /**
     * Get the list of all the items.
     *
     * @return the list of all the items
     */
    List<Item> getItems();

    /**
     * Get an item by its name.
     *
     * @param name the name of the item to get
     * @return the item if found, otherwise an empty Optional
     */
    Optional<Item> getItem(String name);

    /**
     * Get the list of all the names of the items.
     *
     * @return the list of all the names of the items
     */
    List<String> getItemNames();

    /**
     * Give an item to a player.
     *
     * @param sender   the command sender
     * @param player   the player to give the item
     * @param itemName the name of the item to give
     * @param amount   the amount of the item to give
     */
    void giveItem(CommandSender sender, Player player, String itemName, int amount);

    /**
     * Delete all the crafts of the items.
     */
    void deleteCrafts();

    /**
     * Get an item from an item stack.
     *
     * @param itemStack the item stack to get the item from
     * @return the item if found, otherwise an empty Optional
     */
    Optional<Item> getItem(ItemStack itemStack);

    ItemFile getItemFile();
}
