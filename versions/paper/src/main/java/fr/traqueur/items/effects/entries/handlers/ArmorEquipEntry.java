package fr.traqueur.items.effects.entries.handlers;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.traqueur.items.api.annotations.AutoEntry;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.entries.EntryHandler;
import fr.traqueur.items.effects.entries.settings.EmptyEntrySettings;

/**
 * Matches when an armor piece is equipped (Paper-only, {@code PlayerArmorChangeEvent}).
 * <p>
 * No {@code ARMOR_UNEQUIP} counterpart, for the same reason there is no
 * {@code UNHELD}: see {@link fr.traqueur.items.effects.extractors.PlayerArmorChangeExtractor}.
 */
@AutoEntry("ARMOR_EQUIP")
@PaperOnly
public class ArmorEquipEntry implements EntryHandler<EmptyEntrySettings> {

    @Override
    public boolean test(EffectContext context, EmptyEntrySettings settings) {
        return context.event() instanceof PlayerArmorChangeEvent;
    }
}
