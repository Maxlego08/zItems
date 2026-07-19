package fr.traqueur.items.effects.pipeline.entries.combat;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.pipeline.entries.combat.KillEntrySettings;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Matches when a player kills an entity while holding the pipeline's item, optionally
 * restricted to specific vanilla or custom (e.g. MythicMobs) mob types.
 * <p>
 * The item source has already been resolved to the killer's main-hand weapon by
 * {@link fr.traqueur.items.effects.extractors.EntityDeathExtractor} before dispatch.
 */
@AutoEntry("KILL")
public class KillEntry implements EntryHandler<KillEntrySettings> {

    @Override
    public boolean test(EffectContext context, KillEntrySettings settings) {
        if (!(context.event() instanceof EntityDeathEvent event)) {
            return false;
        }

        if (settings.entities() == null || settings.entities().isEmpty()) {
            return true;
        }

        return settings.entities().stream().anyMatch(match -> match.matches(event.getEntity()));
    }
}
