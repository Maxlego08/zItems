package fr.traqueur.items.effects.pipeline.entries.equipment;

import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;
import fr.traqueur.items.effects.events.PlayerArmorUnequipEvent;

/**
 * Matches the synthetic {@link PlayerArmorUnequipEvent} dispatched by the Paper-only
 * {@code ArmorChangeTransitionListener}.
 * <p>
 * Lives in the root module, not {@code versions/paper}: unlike {@code ArmorEquipEntry},
 * this only ever references the platform-agnostic synthetic event (in {@code :common}),
 * never the real {@code PlayerArmorChangeEvent} — no {@code @PaperOnly} needed here.
 * On plain Spigot it simply never matches, since nothing ever constructs that event.
 */
@AutoEntry("ARMOR_UNEQUIP")
public class ArmorUnequipEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerArmorUnequipEvent;
    }
}
