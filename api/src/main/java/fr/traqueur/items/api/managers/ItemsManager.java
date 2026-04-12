package fr.traqueur.items.api.managers;

import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.settings.models.RecipeWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * Manager responsible for building custom items and generating their recipes.
 *
 * <p>This manager provides the primary API for creating and identifying custom items
 * defined in the {@code items/} configuration directory. It handles item building,
 * recipe generation, and custom item detection via persistent data.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Building ItemStacks from {@link Item} definitions</li>
 *   <li>Generating Bukkit recipes (crafting, smithing, etc.) from item configurations</li>
 *   <li>Identifying custom items via PersistentDataContainer tags</li>
 *   <li>Managing the item building lifecycle and events</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Checking if an ItemStack is a Custom Item</h3>
 * <pre>{@code
 * ItemsManager manager = (ItemsManager) Bukkit.getServicesManager()
 *     .load(ItemsManager.class);
 *
 * Optional<Item> customItem = manager.getCustomItem(playerItem);
 * if (customItem.isPresent()) {
 *     // This is a custom zItems item
 *     String itemId = customItem.get().id();
 *     // Handle custom item logic
 * } else {
 *     // This is a vanilla item or from another plugin
 * }
 * }</pre>
 *
 * <h3>Generating Recipes After Configuration Reload</h3>
 * <pre>{@code
 * // After reloading item configurations
 * ItemsRegistry registry = Registry.get(ItemsRegistry.class);
 * registry.reload(); // Reloads items from YAML
 *
 * ItemsManager manager = (ItemsManager) Bukkit.getServicesManager()
 *     .load(ItemsManager.class);
 * manager.generateRecipesFromLoadedItems(); // Registers recipes with Bukkit
 * }</pre>
 *
 * <h2>Implementation Details</h2>
 * <p>The manager implementation (typically {@code ZItemsManager}) coordinates:</p>
 * <ul>
 *   <li>Item building via {@link Item#build(org.bukkit.entity.Player, int)}</li>
 *   <li>Recipe generation from {@link RecipeWrapper}</li>
 *   <li>PDC tagging with unique item identifiers</li>
 *   <li>Event firing ({@link fr.traqueur.items.api.events.ItemBuildEvent})</li>
 * </ul>
 *
 * @see Item
 * @see fr.traqueur.items.api.registries.ItemsRegistry
 */
public non-sealed interface ItemsManager extends Manager {

    /**
     * Generates and registers Bukkit recipes for all loaded custom items.
     *
     * <p>This method iterates through all items in the {@link fr.traqueur.items.api.registries.ItemsRegistry}
     * and generates their recipes based on {@link RecipeWrapper}.
     * Recipes are registered with Bukkit's server and become available in crafting tables,
     * furnaces, smithing tables, etc.</p>
     *
     * <p><b>When to call:</b></p>
     * <ul>
     *   <li>During plugin initialization (after items are loaded)</li>
     *   <li>After reloading item configurations</li>
     *   <li>After adding new items dynamically</li>
     * </ul>
     *
     * <p><b>Recipe Types Supported:</b></p>
     * <ul>
     *   <li>Shaped crafting recipes</li>
     *   <li>Shapeless crafting recipes</li>
     *   <li>Furnace smelting recipes</li>
     *   <li>Blast furnace recipes</li>
     *   <li>Smoker recipes</li>
     *   <li>Campfire cooking recipes</li>
     *   <li>Smithing table recipes (transform and trim)</li>
     *   <li>Stonecutter recipes</li>
     * </ul>
     *
     * <p><b>Important:</b> Existing recipes with the same key are removed before
     * registration to prevent duplicates when reloading.</p>
     *
     * @see RecipeWrapper
     */
    void generateRecipesFromLoadedItems();

    /**
     * Retrieves the custom {@link Item} definition associated with an ItemStack.
     *
     * <p>This method checks the ItemStack's {@link org.bukkit.persistence.PersistentDataContainer}
     * for a custom item identifier tag. If found, the corresponding {@link Item} is
     * retrieved from the {@link fr.traqueur.items.api.registries.ItemsRegistry}.</p>
     *
     * <p><b>Detection Method:</b> Items are identified by a unique string ID stored in
     * the PDC during item creation. This allows custom items to be recognized even after
     * modifications like renaming, enchanting, or effect application.</p>
     *
     * <p><b>Usage Examples:</b></p>
     * <pre>{@code
     * // Check if a player is holding a custom item
     * ItemStack hand = player.getInventory().getItemInMainHand();
     * Optional<Item> customItem = itemsManager.getCustomItem(hand);
     *
     * customItem.ifPresent(item -> {
     *     player.sendMessage("You're holding: " + item.id());
     * });
     * }</pre>
     *
     * <pre>{@code
     * // Filter custom items from inventory
     * Arrays.stream(player.getInventory().getContents())
     *     .filter(Objects::nonNull)
     *     .map(itemsManager::getCustomItem)
     *     .filter(Optional::isPresent)
     *     .map(Optional::get)
     *     .forEach(item -> {
     *         // Process each custom item
     *     });
     * }</pre>
     *
     * @param itemStack the ItemStack to check for custom item data
     * @return an {@link Optional} containing the {@link Item} if this is a custom item,
     *         or {@link Optional#empty()} if this is a vanilla item or the item definition
     *         is no longer loaded (e.g., after configuration removal)
     * @throws NullPointerException if itemStack is null
     */
    Optional<Item> getCustomItem(ItemStack itemStack);
}
