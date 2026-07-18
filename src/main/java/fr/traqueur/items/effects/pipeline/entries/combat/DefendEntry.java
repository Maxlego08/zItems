package fr.traqueur.items.effects.pipeline.entries.combat;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import fr.traqueur.items.effects.pipeline.entries.combat.PlayerDefendEvent;

/**
 * Matches the synthetic {@link PlayerDefendEvent} dispatched by
 * {@link fr.traqueur.items.effects.pipeline.entries.combat.DefendTransitionListener} for each
 * equipped item when its wearer takes damage.
 */
@AutoEntry("DEFEND")
public class DefendEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerDefendEvent;
    }
}
