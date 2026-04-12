package fr.traqueur.items.items.metadata.legacy;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.SpigotOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.jetbrains.annotations.Nullable;

/**
 * Food metadata configuration for consumable items.
 * Discriminator key: "food"
 */
@AutoMetadata("food")
@SpigotOnly
public record LegacyFoodMetadata(
        @DefaultInt(4) int nutrition,

        @DefaultDouble(2.4) double saturation,

        @Options(optional = true)
        @DefaultBool(false)
        boolean canAlwaysEat
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;
        FoodComponent food = meta.getFood();
        food.setNutrition(nutrition);
        food.setSaturation((float) saturation);
        food.setCanAlwaysEat(canAlwaysEat);
        meta.setFood(food);
        itemStack.setItemMeta(meta);
    }
}