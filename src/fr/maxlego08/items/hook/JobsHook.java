package fr.maxlego08.items.hook;

import com.gamingmesh.jobs.api.JobsExpGainEvent;
import fr.maxlego08.items.api.hook.Hook;
import fr.maxlego08.items.api.hook.jobs.JobsExpGainEventWrapper;
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

    @Override
    public String getName() {
        return "Jobs";
    }
}
