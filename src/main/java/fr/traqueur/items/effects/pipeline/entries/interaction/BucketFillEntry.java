package fr.traqueur.items.effects.pipeline.entries.interaction;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import org.bukkit.event.player.PlayerBucketFillEvent;

@AutoEntry("BUCKET_FILL")
public class BucketFillEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerBucketFillEvent;
    }
}
