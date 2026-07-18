package fr.traqueur.items.effects.pipeline.entries.interaction;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import org.bukkit.event.player.PlayerItemConsumeEvent;

@AutoEntry("CONSUME")
public class ConsumeEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerItemConsumeEvent;
    }
}
