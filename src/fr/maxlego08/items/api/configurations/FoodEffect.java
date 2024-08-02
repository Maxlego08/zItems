package fr.maxlego08.items.api.configurations;

public record FoodEffect(String type, double probability, int amplifier, int duration, boolean showParticles,
                         boolean showIcon, boolean ambient) {
}