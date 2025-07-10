package fr.maxlego08.items.api.configurations.meta;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

import java.util.List;

public record Food(boolean enable, int nutrition, int saturation, boolean canAlwaysEat, int eatSeconds,
                   List<FoodEffect> effects, String usingConvertsTo) {

    public void addEffect(FoodEffect effect) {
        this.effects.add(effect);
    }


    public void applyToItemMeta(ItemMeta itemMeta, Player player, ItemPlugin plugin) {

        FoodComponent foodComponent = itemMeta.getFood();

        foodComponent.setNutrition(this.nutrition);
        foodComponent.setSaturation(this.saturation);
        foodComponent.setCanAlwaysEat(this.canAlwaysEat);

        /*foodComponent.setEatSeconds(this.eatSeconds);

        List<FoodComponent.FoodEffect> foodEffects = this.effects.stream().map(effect -> {
            PotionEffect potionEffect = new PotionEffect(PotionEffectType.getByName(effect.type()), effect.duration(), effect.amplifier(), effect.ambient(), effect.showParticles(), effect.showIcon());
            return foodComponent.addEffect(potionEffect, effect.probability());
        }).collect(Collectors.toList());

        foodComponent.setEffects(foodEffects);

        if (this.usingConvertsTo != null) {
            plugin.getItemManager().getItem(this.usingConvertsTo).ifPresent(item -> foodComponent.setUsingConvertsTo(item.build(player, 1)));
        }*/

        itemMeta.setFood(foodComponent);
    }


}