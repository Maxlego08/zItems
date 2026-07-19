package fr.traqueur.items.hooks.zjobs.entries;

import fr.maxlego08.jobs.api.event.events.JobExpGainEvent;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;

/**
 * Registered manually by {@link fr.traqueur.items.hooks.zjobs.ZJobsHook}, not scanned
 * via {@code @AutoEntry} — same reasoning as its Jobs-hook counterpart. Registered
 * under the same entry id ("JOBS_EXP_GAIN") since only one of the two Jobs hooks is
 * ever active at a time.
 */
public class ZJobsExpGainEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof JobExpGainEvent;
    }
}
