package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;

public interface JobsExperienceHandler<T extends RuneConfiguration> {

    void jobsGainExperience(ItemPlugin plugin, JobsExpGainEventWrapper event, T runeConfiguration);

}
