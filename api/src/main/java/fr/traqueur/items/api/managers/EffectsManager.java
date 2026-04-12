package fr.traqueur.items.api.managers;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectApplicationResult;
import fr.traqueur.items.api.items.Item;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Manager responsible for applying, managing, and displaying item effects.
 *
 * <p>This manager provides the core API for working with effects that modify item
 * behavior. It handles effect application, validation, lore generation, and the
 * effect representation item system used in smithing tables and applicator GUIs.</p>
 *
 * <h2>Responsibilities</h2>
 * <ul>
 *   <li>Applying effects to items via commands, smithing tables, or applicator GUI</li>
 *   <li>Validating effect compatibility with target items</li>
 *   <li>Generating and updating effect lore on items</li>
 *   <li>Managing effect representation items (items that can be applied to equipment)</li>
 *   <li>Loading and registering effect application recipes</li>
 *   <li>Reapplying passive effects (NoEventEffects) during item fusion</li>
 * </ul>
 *
 * <h2>Effect Application Flow</h2>
 * <pre>{@code
 * // 1. Get the effect from registry
 * EffectsRegistry registry = Registry.get(EffectsRegistry.class);
 * Optional<Effect> effect = registry.get("hammer");
 *
 * // 2. Get the manager
 * EffectsManager manager = (EffectsManager) Bukkit.getServicesManager()
 *     .load(EffectsManager.class);
 *
 * // 3. Check if effect can be applied
 * if (manager.canApplyEffectTo(effect.get(), item)) {
 *     // 4. Apply the effect
 *     EffectApplicationResult result = manager.applyEffect(player, item, effect.get());
 *
 *     switch (result) {
 *         case SUCCESS -> player.sendMessage("Effect applied!");
 *         case INCOMPATIBLE -> player.sendMessage("Incompatible effect!");
 *         case DISABLED -> player.sendMessage("Effect is disabled on this item!");
 *         case ALREADY_APPLIED -> player.sendMessage("Effect already present!");
 *         case NOT_ALLOWED -> player.sendMessage("Cannot add more effects!");
 *     }
 * }
 * }</pre>
 *
 * <h2>Effect Lore System</h2>
 * <p>The manager handles automatic lore generation and updates when effects are
 * added to items. It respects item-specific visibility settings:</p>
 * <ul>
 *   <li><b>nb-effects-view:</b> Limits number of effects shown (-1 = all, 0 = none)</li>
 *   <li><b>base-effects-visible:</b> Shows/hides base effects from item config</li>
 *   <li><b>additional-effects-visible:</b> Shows/hides effects added via commands</li>
 * </ul>
 *
 * <h2>Effect Representation Items</h2>
 * <p>Effects can have physical item representations that can be applied in smithing
 * tables or custom GUIs:</p>
 * <pre>{@code
 * // Check if an item is an effect representation
 * if (manager.isEffectItem(itemStack)) {
 *     Effect effect = manager.getEffectFromItem(itemStack);
 *     // This item can be used to apply 'effect' to equipment
 * }
 *
 * // Create a representation item for an effect
 * ItemStack effectItem = manager.createEffectItem(effect, player);
 * // Player can now use this in a smithing table
 * }</pre>
 *
 * @see Effect
 * @see fr.traqueur.items.api.effects.EffectHandler
 * @see fr.traqueur.items.api.effects.EffectApplicationResult
 * @see fr.traqueur.items.api.registries.EffectsRegistry
 */
public non-sealed interface EffectsManager extends Manager {

    /**
     * Loads all effect application recipes into the system.
     * This includes recipes for applying effects via smithing table or applicator GUI.
     */
    void loadRecipes();

    /**
     * Applies the given effect to the specified item for the player.
     *
     * @param player the player applying the effect (can be null)
     * @param item the item to apply the effect to
     * @param effect the effect to apply
     * @return the result of the effect application
     */
    EffectApplicationResult applyEffect(Player player, ItemStack item, Effect effect);

    /**
     * Checks if an effect can be applied to the given item based on the effect's settings.
     *
     * @param effect the effect to check
     * @param item the item to apply to
     * @return true if the effect can be applied, false otherwise
     */
    boolean canApplyEffectTo(Effect effect, ItemStack item);

    /**
     * Checks if an ItemStack has any custom effects.
     *
     * @param item the item to check
     * @return true if the item has at least one effect, false otherwise
     */
    boolean hasEffects(ItemStack item);

    /**
     * Generates lore lines for the base effects of an item.
     *
     * @param player the player context (can be null)
     * @param baseEffects the list of base effects
     * @param item the custom item (can be null for vanilla items)
     * @return the list of lore components representing the base effects
     */
    List<Component> generateBaseEffectLore(Player player, List<Effect> baseEffects, Item item);

    /**
     * Updates the item's lore to display the given effects.
     * Handles both custom items (with their configuration) and vanilla items.
     *
     * @param player the player context (can be null)
     * @param item the item to update
     * @param effects all effects to display in the lore
     */
    void updateItemLoreWithEffects(Player player, ItemStack item, List<Effect> effects);

    /**
     * Reapplies all NoEventEffects from the given list to the item.
     * This is useful when fusing items in anvils or similar scenarios.
     *
     * @param player the player context (can be null)
     * @param item the item to apply effects to
     * @param effects the list of effects to reapply
     */
    void reapplyNoEventEffects(Player player, ItemStack item, List<Effect> effects);

    /**
     * Checks if an ItemStack is an effect representation item (can be applied to equipment).
     *
     * @param item the item to check
     * @return true if this is an effect representation item
     */
    boolean isEffectItem(ItemStack item);

    /**
     * Gets the effect represented by this item, if any.
     *
     * @param item the item to check
     * @return the effect this item represents, or null if not an effect item
     */
    Effect getEffectFromItem(ItemStack item);

    /**
     * Creates an ItemStack that represents an effect and can be applied to equipment.
     *
     * @param effect the effect to create an item for
     * @param player the player context (for custom items)
     * @return the effect representation ItemStack, or null if effect has no representation
     */
    ItemStack createEffectItem(Effect effect, Player player);
}
