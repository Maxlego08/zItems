package fr.maxlego08.items.api.configurations;

import java.util.List;

public record Food(int nutrition, int saturation, boolean isMeat, boolean canAlwaysEat, int eatSeconds,
                   List<FoodEffect> effects) {

    public void addEffect(FoodEffect effect) {
        this.effects.add(effect);
    }
}