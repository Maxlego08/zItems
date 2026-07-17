package fr.traqueur.items.entries.handlers;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.entries.EntryHandler;
import fr.traqueur.items.entries.settings.EmptyEntrySettings;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Matches when a player kills an entity while holding the pipeline's item.
 * <p>
 * The item source has already been resolved to the killer's main-hand weapon by
 * {@link fr.traqueur.items.effects.extractors.EntityDeathExtractor} before dispatch,
 * so this only needs to check the event type.
 */
@AutoEntry("KILL")
public class KillEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof EntityDeathEvent;
    }
}
