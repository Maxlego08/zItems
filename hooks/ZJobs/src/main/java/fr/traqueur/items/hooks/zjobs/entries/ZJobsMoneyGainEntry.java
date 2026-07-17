package fr.traqueur.items.hooks.zjobs.entries;

import fr.maxlego08.jobs.api.event.events.JobMoneyGainEvent;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;

/**
 * Registered manually by {@link fr.traqueur.items.hooks.zjobs.ZJobsHook}, not scanned
 * via {@code @AutoEntry} — see {@link ZJobsExpGainEntry}.
 */
public class ZJobsMoneyGainEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof JobMoneyGainEvent;
    }
}
