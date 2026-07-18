package fr.traqueur.items.effects.engine;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.engine.EmptySettings;

@AutoEffect(value = "EMPTY")
public class Empty implements EffectHandler.NoEventEffectHandler<EmptySettings> {

    @Override
    public void handle(EffectContext context, EmptySettings settings) {
        // No effect
    }

    @Override
    public int priority() {
        return -1;
    }
}
