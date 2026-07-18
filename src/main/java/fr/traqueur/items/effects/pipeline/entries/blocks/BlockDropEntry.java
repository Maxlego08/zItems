package fr.traqueur.items.effects.pipeline.entries.blocks;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import org.bukkit.event.block.BlockDropItemEvent;

@AutoEntry("BLOCK_DROP")
public class BlockDropEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof BlockDropItemEvent;
    }
}
