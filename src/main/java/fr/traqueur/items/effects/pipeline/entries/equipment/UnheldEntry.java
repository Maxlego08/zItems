package fr.traqueur.items.effects.pipeline.entries.equipment;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import fr.traqueur.items.effects.pipeline.entries.equipment.PlayerItemUnheldEvent;

/**
 * Matches the synthetic {@link PlayerItemUnheldEvent} dispatched by
 * {@link fr.traqueur.items.effects.pipeline.entries.equipment.HeldTransitionListener}.
 */
@AutoEntry("UNHELD")
public class UnheldEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerItemUnheldEvent;
    }
}
