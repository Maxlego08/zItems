package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.settings.models.PotionEffectWrapper;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AutoMetadata("food")
@PaperOnly
public record FoodMetadata(
        int nutrition,
        double saturation,
        @Options(optional = true) @DefaultBool(false) boolean canAlwaysEat,
        @Options(optional = true) List<PotionEffectWrapper> effects,
        @Options(optional = true) @DefaultDouble(-1) double eatSeconds,
        @Options(optional = true) ItemUseAnimation animation,
        @Options(optional = true) Sound sound,
        @Options(optional = true) @DefaultDouble(-1) double cooldownSeconds,
        @Options(optional = true) String groupCooldown
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        Consumable.Builder builder = null;

        if (effects != null && !effects.isEmpty()) {
            List<PotionEffect> potionEffects = effects.stream()
                    .map(PotionEffectWrapper::toPotionEffect)
                    .toList();
            builder = Consumable.consumable()
                    .addEffect(ConsumeEffect.applyStatusEffects(potionEffects, 1.0f));
        }

        if (eatSeconds > 0) {
            if (builder == null) {
                builder = Consumable.consumable();
            }
            builder.consumeSeconds((float) eatSeconds);
        }

        if (animation != null) {
            if (builder == null) {
                builder = Consumable.consumable();
            }
            builder.animation(animation);
        }

        if (sound != null) {
            if (builder == null) {
                builder = Consumable.consumable();
            }
            Key key = RegistryAccess.registryAccess().getRegistry(RegistryKey.SOUND_EVENT).getKey(sound);
            if (key != null) {
                builder.sound(key);
            }
        }

        if (builder != null) {
            itemStack.setData(DataComponentTypes.CONSUMABLE, builder.build());
        }

        if (cooldownSeconds > 0) {
            UseCooldown.Builder useCoolDownBuilder = UseCooldown.useCooldown((float) cooldownSeconds);
            if (groupCooldown != null && !groupCooldown.isEmpty()) {
                useCoolDownBuilder.cooldownGroup(Key.key(groupCooldown));
            }
            itemStack.setData(DataComponentTypes.USE_COOLDOWN, useCoolDownBuilder.build());
        }

        FoodProperties properties = FoodProperties.food()
                .canAlwaysEat(true)
                .nutrition(nutrition)
                .saturation((float) saturation).build();

        itemStack.setData(DataComponentTypes.FOOD, properties);
    }
}