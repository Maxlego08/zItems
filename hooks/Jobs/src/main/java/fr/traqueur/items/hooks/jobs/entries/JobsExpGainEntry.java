package fr.traqueur.items.hooks.jobs.entries;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;

/**
 * Registered manually by {@link fr.traqueur.items.hooks.jobs.JobsHook}, not scanned via
 * {@code @AutoEntry}: this class references a Jobs-plugin event type that doesn't exist
 * on servers without Jobs installed, so it must only ever be touched when the hook
 * actually enables.
 */
public class JobsExpGainEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof JobsExpGainEvent;
    }
}
