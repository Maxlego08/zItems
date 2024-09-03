package fr.maxlego08.items.hook.jobs;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import fr.maxlego08.items.api.runes.RuneManager;
import org.bukkit.event.EventHandler;

public class JobsHook implements Hook {

    private final RuneManager runeManager;


    public JobsHook(RuneManager runeManager) {
        this.runeManager = runeManager;
    }

    @EventHandler
    public void onExpGain(JobsExpGainEvent event) {
        JobsExpGainEventWrapper jobsExpGainEvent = new JobsExpGainEventWrapper(event.getPlayer().getPlayer(), event.getExp());
        this.runeManager.onPlayerEvent(jobsExpGainEvent);

        if (jobsExpGainEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setExp(jobsExpGainEvent.getExp());
    }

    @EventHandler
    public void onPayement(JobsPrePaymentEvent event) {
        JobsPayementEventWrapper jobsPayementEvent = new JobsPayementEventWrapper(event.getPlayer().getPlayer(), event.getAmount());
        this.runeManager.onPlayerEvent(jobsPayementEvent);

        if (jobsPayementEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setAmount(jobsPayementEvent.getAmount());
    }

    @Override
    public String getName() {
        return "Jobs";
    }
}
