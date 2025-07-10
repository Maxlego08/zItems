package fr.maxlego08.items.api.runes;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;


public interface RuneActivator {

    default void applyDamageToItem(ItemStack itemStack, int damage, LivingEntity livingEntity) {
        itemStack.damage(damage, livingEntity);
    }

    int getPriority();
}
