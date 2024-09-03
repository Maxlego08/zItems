package fr.maxlego08.items.api.hook.jobs;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobsExpGainEventWrapper extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled = false;
    private final Player player;
    private double exp;

    public JobsExpGainEventWrapper(Player player, double exp) {
        this.player = player;
        this.exp = exp;
    }

    public Player getPlayer() {
        return player;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
