package fr.traqueur.items.persistence;

import fr.traqueur.items.serialization.EffectDataType;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.jetbrains.annotations.NotNull;

public class ZEffectDataType extends EffectDataType {

    private ZEffectDataType() {
    }

    public static void initialize() {
        EffectDataType.INSTANCE = new ZEffectDataType();
    }

    @Override
    public @NotNull String toPrimitive(@NotNull Effect complex, @NotNull PersistentDataAdapterContext context) {
        return complex.id();
    }

    @Override
    public @NotNull Effect fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return Registry.get(EffectsRegistry.class).getById(primitive);
    }
}
