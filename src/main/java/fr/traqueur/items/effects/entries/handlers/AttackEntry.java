package fr.traqueur.items.effects.entries.handlers;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Matches when a player deals damage while holding the pipeline's item.
 * <p>
 * The item source has already been resolved to the attacker's main-hand weapon by
 * {@link fr.traqueur.items.effects.extractors.EntityDamageByEntityExtractor} before
 * dispatch, so this only needs to check the event type.
 */
@AutoEntry("ATTACK")
public class AttackEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof EntityDamageByEntityEvent;
    }
}
