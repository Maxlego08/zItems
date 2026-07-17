package fr.traqueur.items.effects.entries.handlers;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import fr.traqueur.items.effects.events.PlayerTickEvent;

/**
 * Matches on every {@link PlayerTickEvent} while the player is sneaking — an ongoing
 * state, not a discrete event, so this is checked live rather than gated on a
 * one-shot toggle.
 */
@AutoEntry("SNEAKING")
public class SneakingEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerTickEvent && context.executor().isSneaking();
    }
}
