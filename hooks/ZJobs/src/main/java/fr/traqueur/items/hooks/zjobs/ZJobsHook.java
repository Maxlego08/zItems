package fr.traqueur.items.hooks.zjobs;

import fr.maxlego08.jobs.api.event.events.JobExpGainEvent;
import fr.maxlego08.jobs.api.event.events.JobMoneyGainEvent;
import fr.traqueur.items.api.annotations.AutoHook;
import fr.traqueur.items.api.hooks.Hook;
import fr.traqueur.items.api.registries.EntryHandlersRegistry;
import fr.traqueur.items.api.registries.ExtractorsRegistry;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.hooks.zjobs.entries.ZJobsExpGainEntry;
import fr.traqueur.items.hooks.zjobs.entries.ZJobsMoneyGainEntry;
import fr.traqueur.items.hooks.zjobs.extractor.ZJobExpGainEventExtractor;
import fr.traqueur.items.hooks.zjobs.extractor.ZJobMoneyGainEventExtractor;
import fr.traqueur.items.hooks.zjobs.handlers.ZJobsExperienceMultiplier;
import fr.traqueur.items.hooks.zjobs.handlers.ZJobsMoneyMultiplier;

@AutoHook("zJobs")
public class ZJobsHook implements Hook {
    @Override
    public void onEnable() {
        HandlersRegistry handlersRegistry = Registry.get(HandlersRegistry.class);
        ExtractorsRegistry extractorsRegistry = Registry.get(ExtractorsRegistry.class);
        EntryHandlersRegistry entryHandlersRegistry = Registry.get(EntryHandlersRegistry.class);

        extractorsRegistry.register(JobExpGainEvent.class, new ZJobExpGainEventExtractor());
        extractorsRegistry.register(JobMoneyGainEvent.class, new ZJobMoneyGainEventExtractor());

        handlersRegistry.register("JOB_XP_BOOST", new ZJobsExperienceMultiplier());
        handlersRegistry.register("JOB_MONEY_BOOST", new ZJobsMoneyMultiplier());

        entryHandlersRegistry.register("JOBS_EXP_GAIN", new ZJobsExpGainEntry());
        entryHandlersRegistry.register("JOBS_MONEY_GAIN", new ZJobsMoneyGainEntry());
    }
}
