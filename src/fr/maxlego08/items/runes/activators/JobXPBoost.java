package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.runes.configurations.RuneXPBoostConfiguration;

public class JobXPBoost extends RuneActivatorHelper<RuneXPBoostConfiguration>{

    @Override
    public void jobsGainExperience(ItemPlugin plugin, JobsExpGainEventWrapper event, RuneXPBoostConfiguration runeConfiguration) {
        event.setExp(event.getExp() * runeConfiguration.getXpBoost());
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
