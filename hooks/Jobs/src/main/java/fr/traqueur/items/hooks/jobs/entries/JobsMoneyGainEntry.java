package fr.traqueur.items.hooks.jobs.entries;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;

/**
 * Registered manually by {@link fr.traqueur.items.hooks.jobs.JobsHook}, not scanned via
 * {@code @AutoEntry}: see {@link JobsExpGainEntry}.
 */
public class JobsMoneyGainEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof JobsPrePaymentEvent;
    }
}
