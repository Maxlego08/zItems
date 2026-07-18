package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;

public class EffectReader implements Reader<Effect> {
    @Override
    public Effect read(String s) throws StructuraException {
        Effect effect = Registry.get(EffectsRegistry.class).getById(s);
        if (effect == null) {
            throw new StructuraException("Effect " + s + " not found in EffectsRegistry");
        }
        return effect;
    }
}