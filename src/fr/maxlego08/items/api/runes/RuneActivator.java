package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;


public interface RuneActivator<T extends RuneConfiguration> {

    default void applyDamageToItem(ItemStack itemStack, int damage, LivingEntity livingEntity) {
        itemStack.damage(damage, livingEntity);
    }

    int getPriority();
}
