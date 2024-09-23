package fr.maxlego08.items.hook.jobs;

import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.jobs.api.event.events.JobExpGainEvent;
import fr.maxlego08.jobs.api.event.events.JobMoneyGainEvent;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.RuneManager;
import org.bukkit.event.EventHandler;

public class ZJobsHook implements Hook {

    private final RuneManager runeManager;

    public ZJobsHook(RuneManager runeManager) {
        this.runeManager = runeManager;
    }

    @EventHandler
    public void onExpGain(JobExpGainEvent event) {
        JobsExpGainEventWrapper jobsExpGainEvent = new JobsExpGainEventWrapper(event.getPlayer().getPlayer(), event.getExperience());
        this.runeManager.onPlayerEvent(jobsExpGainEvent);

        if (jobsExpGainEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setExperience(jobsExpGainEvent.getExp());
    }

    @EventHandler
    public void onPayement(JobMoneyGainEvent event) {
        JobsPayementEventWrapper jobsPayementEvent = new JobsPayementEventWrapper(event.getPlayer().getPlayer(), event.getMoney());
        this.runeManager.onPlayerEvent(jobsPayementEvent);

        if (jobsPayementEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setMoney(jobsPayementEvent.getAmount());
    }

}


