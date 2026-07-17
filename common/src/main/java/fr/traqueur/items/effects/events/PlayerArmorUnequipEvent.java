package fr.traqueur.items.effects.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Synthetic event, never fired through Bukkit's event bus — constructed and passed
 * directly to {@code EffectsDispatcher#dispatch} by the Paper-only transition listener
 * that watches {@code PlayerArmorChangeEvent}.
 * <p>
 * Lives in {@code :common} (not the root module, not {@code versions/paper}) because
 * it carries no Paper dependency of its own and needs to be visible both to the
 * Paper-only listener that constructs it and to the platform-agnostic
 * {@code ArmorUnequipEntry} that gates on it — only the listener actually touches the
 * real Paper event class.
 */
public class PlayerArmorUnequipEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public PlayerArmorUnequipEvent(Player player) {
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
