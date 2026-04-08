package fr.traqueur.items.api.managers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Manager responsible for applying durability damage to items.
 * Handles both {@link fr.traqueur.items.api.items.DurabilityMode#VANILLA} and
 * {@link fr.traqueur.items.api.items.DurabilityMode#CUSTOM} modes transparently.
 */
public non-sealed interface DurabilityManager extends Manager {

    /**
     * Applies damage to an item, respecting the item's durability mode.
     * <ul>
     *   <li>VANILLA mode: delegates to {@link fr.traqueur.items.api.utils.ItemUtil#applyDamageToItem}</li>
     *   <li>CUSTOM mode: decrements PDC-tracked durability; removes item when reaching 0</li>
     * </ul>
     *
     * @param item   the item to damage
     * @param damage the amount of durability to remove
     * @param player the player holding the item
     */
    void applyDamage(ItemStack item, int damage, Player player);
}
