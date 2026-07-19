package fr.traqueur.items.effects.pipeline.entries.equipment;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Synthetic event, never fired through Bukkit's event bus — constructed and passed
 * directly to {@link fr.traqueur.items.api.effects.EffectsDispatcher#dispatch} by
 * {@link fr.traqueur.items.effects.pipeline.entries.equipment.HeldTransitionListener}.
 * <p>
 * {@code PlayerItemHeldEvent} only ever lets a dispatch resolve the item about to
 * become held (see {@link fr.traqueur.items.effects.extractors.PlayerItemHeldExtractor}) —
 * one dispatch can't resolve two items from one real event. This wraps the "item that
 * just stopped being held" direction so {@code UNHELD} entries can gate on it like any
 * other event.
 */
public class PlayerItemUnheldEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerItemUnheldEvent(Player player) {
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
