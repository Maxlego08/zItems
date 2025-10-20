package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.runes.applicators.Applicator;
import fr.maxlego08.items.api.runes.exceptions.RuneException;
import fr.traqueur.recipes.impl.domains.ItemRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RuneManager {

    /**
     * Loads all the runes from the configuration files.
     * This method is called when the plugin is enabled.
     */
    void loadRunes();

    /**
     * Loads all the crafts from the configuration files that uses runes.
     * This method is called when the plugin is enabled.
     */
    void loadCraftWithRunes();

    /**
     * Loads a rune from a configuration file.
     * This method is called when the plugin is enabled.
     *
     * @param file the file to load
     */
    void loadRune(File file);

    /**
     * Get a rune by its name.
     *
     * @param name the name of the rune to get
     * @return the rune if found, otherwise an empty Optional
     */
    Optional<Rune> getRune(String name);

    /**
     * Get all the runes.
     *
     * @return a list of all the runes
     */
    List<Rune> getRunes();

    /**
     * Gets all the runes that have the given type.
     *
     * @param runeType the type of the runes to get
     * @return a list of all the runes that have the given type
     */
    List<Rune> getRunes(RuneType runeType);

    /**
     * Gets all the runes from an item stack.
     *
     * @param itemStack the item stack to get the runes from
     * @return an Optional containing a list of all the runes if found, otherwise an empty Optional
     */
    Optional<List<Rune>> getRunes(ItemStack itemStack);

    /**
     * Applies a rune to an item stack of a player.
     *
     * @param player  the player who owns the item stack
     * @param runName the name of the rune to apply
     */
    void applyRune(Player player, String runName);

    /**
     * Applies a rune to an item stack.
     * This method will throw a RuneException if the rune cannot be applied to the item stack.
     *
     * @param itemStack the item stack to apply the rune to
     * @param rune      the rune to apply
     * @throws RuneException if the rune cannot be applied to the item stack
     */
    void applyRune(ItemStack itemStack, Rune rune) throws RuneException;

    /**
     * Gets the NamespacedKey used to store the runes in an item stack's metadata.
     *
     * @return the NamespacedKey used to store the runes in an item stack's metadata
     */
    NamespacedKey getKey();

    /**
     * Gets the NamespacedKey used to store the rune that represents an item stack in its metadata.
     *
     * @return the NamespacedKey used to store the rune that represents an item stack in its metadata
     */
    NamespacedKey getRuneRepresentKey();

    /**
     * Gets the PersistentDataType used to store a rune in an item stack's metadata.
     *
     * @return the PersistentDataType used to store a rune in an item stack's metadata
     */
    PersistentDataType<String, Rune> getDataType();

    /**
     * Deletes all the crafts associated with the runes.
     * This method should be called when the plugin is disabled.
     */
    void deleteCrafts();

    /**
     * Handles a player event.
     * This method is called when a player event is fired.
     *
     * @param event the player event to handle
     * @param <T>   the type of the player event
     */
    <T extends PlayerEvent> void onPlayerEvent(T event);

    /**
     * Gets all the applicators.
     *
     * @return a list of all the applicators
     */
    List<Applicator> getApplicators();

    /**
     * Gets a map of all the runes to their associated recipes.
     * This map contains all the recipes that use at least one rune.
     * The key of the map is the rune and the value is a list of all the recipes that uses the rune.
     *
     * @return a map of all the runes to their associated recipes
     */
    Map<Rune, List<ItemRecipe>> getRecipesUseRunes();
}
