package fr.maxlego08.items.hook;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
import fr.maxlego08.items.api.hook.jobs.JobsPayementEventWrapper;
import org.bukkit.event.EventHandler;

public class JobsHook implements Hook {

    @EventHandler
    public void onExpGain(JobsExpGainEvent event) {
        JobsExpGainEventWrapper zEvent = new JobsExpGainEventWrapper(event.getPlayer().getPlayer(), event.getExp());
        zEvent.callEvent();
        if (zEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setExp(zEvent.getExp());
    }

    @EventHandler
    public void onPayement(JobsPrePaymentEvent event) {
        JobsPayementEventWrapper zEvent = new JobsPayementEventWrapper(event.getPlayer().getPlayer(), event.getAmount());
        zEvent.callEvent();
        if (zEvent.isCancelled()) {
            event.setCancelled(true);
            return;
        }
        event.setAmount(zEvent.getAmount());
    }

    @Override
    public String getName() {
        return "Jobs";
    }
}
