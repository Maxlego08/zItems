package fr.traqueur.items.effects.pipeline.entries.combat;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

/**
 * Synthetic event, never fired through Bukkit's event bus — constructed and passed
 * directly to {@code EffectsDispatcher#dispatch} by
 * {@link fr.traqueur.items.effects.pipeline.entries.combat.DefendTransitionListener}, once per
 * equipped item, when a player takes damage.
 * <p>
 * Deliberately NOT a subtype of {@code EntityDamageEvent}/{@code EntityDamageByEntityEvent}:
 * the {@code ATTACK} entry tests {@code instanceof EntityDamageByEntityEvent} against the
 * attacker's weapon dispatch, and reusing the raw damage event here (on the victim's gear)
 * would make an {@code ATTACK}-gated pipeline on a piece of armor fire when its wearer is
 * hit, not when they hit someone — the exact opposite of what {@code ATTACK} means. Wrapping
 * keeps the two dispatches completely unambiguous while still exposing the real event via
 * {@link #getDamageEvent()} for steps that need it (e.g. to reduce {@code setDamage}).
 */
public class PlayerDefendEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final EntityDamageEvent damageEvent;

    public PlayerDefendEvent(Player player, EntityDamageEvent damageEvent) {
        super(player);
        this.damageEvent = damageEvent;
    }

    public EntityDamageEvent getDamageEvent() {
        return damageEvent;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
