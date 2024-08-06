package fr.maxlego08.items.api.configurations.meta;

public record FoodEffect(String type, float probability, int amplifier, int duration, boolean showParticles,
                         boolean showIcon, boolean ambient) {

}