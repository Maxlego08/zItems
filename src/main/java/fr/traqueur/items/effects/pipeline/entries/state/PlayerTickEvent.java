package fr.traqueur.items.effects.pipeline.entries.state;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Synthetic event, never fired through Bukkit's event bus — constructed and passed
 * directly to {@code EffectsDispatcher#dispatch} by
 * {@link fr.traqueur.items.effects.pipeline.entries.state.TickDispatcher} on a fixed interval, for every
 * online player's main-hand item.
 * <p>
 * Unlike every other entry, there is no real Bukkit event to gate on for "while a
 * state holds" conditions (sneaking, sprinting, etc.) — those aren't discrete
 * moments, they're ongoing. Entries like {@code SNEAKING} test the player's live
 * state (e.g. {@code Player#isSneaking()}) against this periodic tick instead.
 */
public class PlayerTickEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerTickEvent(Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
