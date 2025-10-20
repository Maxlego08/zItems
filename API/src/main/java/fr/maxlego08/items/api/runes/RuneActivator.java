package fr.maxlego08.items.api.runes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;


public interface RuneActivator {

    /**
     * Applies damage to an item stack.
     * This method will apply damage to the given item stack, using the given amount of damage and the given living entity.
     * If the item stack's durability is 0 after applying the damage, the item stack will be removed from the player's inventory.
     *
     * @param itemStack    the item stack to apply the damage to
     * @param damage       the amount of damage to apply
     * @param livingEntity the living entity that is applying the damage
     */
    default void applyDamageToItem(ItemStack itemStack, int damage, LivingEntity livingEntity) {
        itemStack.damage(damage, livingEntity);
    }

    /**
     * Returns the priority of this RuneActivator.
     *
     * @return the priority of this RuneActivator
     */
    int getPriority();
}
