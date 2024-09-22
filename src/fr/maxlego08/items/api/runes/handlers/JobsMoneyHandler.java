package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;

public interface JobsMoneyHandler<T extends RuneConfiguration> {

    void jobsGainMoney(ItemPlugin plugin, JobsPayementEventWrapper event, T runeConfiguration);

}
