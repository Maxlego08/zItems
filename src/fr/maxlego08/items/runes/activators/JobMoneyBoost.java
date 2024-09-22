package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.configurations.RuneMoneyBoostConfiguration;
import fr.maxlego08.items.api.runes.handlers.JobsMoneyHandler;

public class JobMoneyBoost implements JobsMoneyHandler<RuneMoneyBoostConfiguration>, RuneActivator {

    @Override
    public void jobsGainMoney(ItemPlugin plugin, JobsPayementEventWrapper event, RuneMoneyBoostConfiguration runeConfiguration) {
        event.setAmount(event.getAmount() * runeConfiguration.getMoneyBoost());
    }

    @Override
    public int getPriority() {
        return -1;
    }
}
