package fr.traqueur.items.effects.entries.handlers;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.CropsEntrySettings;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * Matches when a mature crop is harvested, optionally restricted to specific crop
 * materials. Reuses the same "mature plant" check as {@link fr.traqueur.items.effects.handlers.FarmingHoe}
 * ({@code Ageable} at max age), independent of that effect's area-harvest logic.
 */
@AutoEntry("CROPS")
public class CropsEntry implements EntryHandler<CropsEntrySettings> {

    @Override
    public boolean test(EffectContext context, CropsEntrySettings settings) {
        if (!(context.event() instanceof BlockBreakEvent event)) {
            return false;
        }

        Block block = event.getBlock();
        if (!(block.getBlockData() instanceof Ageable ageable) || ageable.getAge() != ageable.getMaximumAge()) {
            return false;
        }

        return settings.materials() == null || settings.materials().isEmpty() || settings.materials().contains(block.getType());
    }
}
