package fr.traqueur.items.hooks.jobs;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import fr.traqueur.items.api.annotations.AutoHook;
import fr.traqueur.items.api.hooks.Hook;
import fr.traqueur.items.api.registries.EntryHandlersRegistry;
import fr.traqueur.items.api.registries.ExtractorsRegistry;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.hooks.jobs.entries.JobsExpGainEntry;
import fr.traqueur.items.hooks.jobs.entries.JobsMoneyGainEntry;
import fr.traqueur.items.hooks.jobs.extractors.JobExpGainEventExtractor;
import fr.traqueur.items.hooks.jobs.extractors.JobMoneyGainEventExtractor;
import fr.traqueur.items.hooks.jobs.handlers.JobsExperienceMultiplier;
import fr.traqueur.items.hooks.jobs.handlers.JobsMoneyMultiplier;

@AutoHook("Jobs")
public class JobsHook implements Hook {
    @Override
    public void onEnable() {
        HandlersRegistry handlersRegistry = Registry.get(HandlersRegistry.class);
        ExtractorsRegistry extractorsRegistry = Registry.get(ExtractorsRegistry.class);
        EntryHandlersRegistry entryHandlersRegistry = Registry.get(EntryHandlersRegistry.class);

        extractorsRegistry.register(JobsExpGainEvent.class, new JobExpGainEventExtractor());
        extractorsRegistry.register(JobsPrePaymentEvent.class, new JobMoneyGainEventExtractor());

        handlersRegistry.register("JOB_XP_BOOST", new JobsExperienceMultiplier());
        handlersRegistry.register("JOB_MONEY_BOOST", new JobsMoneyMultiplier());

        entryHandlersRegistry.register("JOBS_EXP_GAIN", new JobsExpGainEntry());
        entryHandlersRegistry.register("JOBS_MONEY_GAIN", new JobsMoneyGainEntry());
    }
}
