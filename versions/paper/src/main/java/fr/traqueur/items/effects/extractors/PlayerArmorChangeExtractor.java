package fr.traqueur.items.effects.extractors;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.annotations.PaperOnly;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;

/**
 * Extractor for PlayerArmorChangeEvent (Paper-only, {@code com.destroystokyo.paper}).
 * <p>
 * Extracts the armor piece being put on. There is no "unequip" counterpart: a
 * dispatch only ever resolves one item per event, and {@link #extract} already
 * picks the new-item direction — same limitation as
 * {@link PlayerItemHeldExtractor} for {@code HELD}/{@code UNHELD}.
 */
@AutoExtractor(PlayerArmorChangeEvent.class)
@PaperOnly
public class PlayerArmorChangeExtractor implements ItemSourceExtractor<PlayerArmorChangeEvent> {

    @Override
    public ExtractionResult extract(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getNewItem());
    }
}
