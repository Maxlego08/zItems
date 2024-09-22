package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneXPBoostConfiguration;
import fr.maxlego08.items.api.runes.handlers.JobsExperienceHandler;

public class JobXPBoost implements JobsExperienceHandler<RuneXPBoostConfiguration>, RuneActivator<RuneXPBoostConfiguration> {

    @Override
    public void jobsGainExperience(ItemPlugin plugin, JobsExpGainEventWrapper event, RuneXPBoostConfiguration runeConfiguration) {
        event.setExp(event.getExp() * runeConfiguration.getXpBoost());
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
