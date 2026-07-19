package fr.traqueur.items.effects.pipeline.entries.equipment;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * Matches when an item becomes held (selected into the main hand).
 * <p>
 * There is no {@code UNHELD} counterpart: {@link fr.traqueur.items.effects.extractors.PlayerItemHeldExtractor}
 * resolves the item source to the new slot, and a dispatch only ever resolves
 * one item per event — so the "just stopped being held" direction of this
 * same event cannot be observed without a second, separate dispatch.
 */
@AutoEntry("HELD")
public class HeldEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerItemHeldEvent;
    }
}
