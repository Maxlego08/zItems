package fr.traqueur.items.effects.pipeline.entries.blocks;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.pipeline.entries.blocks.MiningEntrySettings;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Matches when a block is broken, optionally restricted to specific vanilla or custom
 * (e.g. ItemsAdder/Nexo/Oraxen) block types. Replaces the old unfiltered BLOCK_BREAK
 * entry entirely — an empty/absent {@code materials} list matches any block, so there
 * is never a need for both a filtered and an unfiltered mining entry side by side.
 */
@AutoEntry("MINING")
public class MiningEntry implements EntryHandler<MiningEntrySettings> {

    @Override
    public boolean test(EffectContext context, MiningEntrySettings settings) {
        if (!(context.event() instanceof BlockBreakEvent event)) {
            return false;
        }

        if (settings.materials() == null || settings.materials().isEmpty()) {
            return true;
        }

        return settings.materials().stream().anyMatch(match -> match.matches(event.getBlock()));
    }
}
