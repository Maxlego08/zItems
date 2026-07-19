package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

/**
 * Extractor for EntityShootBowEvent.
 * <p>
 * Not a {@code PlayerEvent} — extracts the bow/crossbow used, only when a
 * player is the one shooting.
 */
@AutoExtractor(EntityShootBowEvent.class)
public class EntityShootBowExtractor implements ItemSourceExtractor<EntityShootBowEvent> {

    @Override
    public ExtractionResult extract(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return null;
        }
        return new ExtractionResult(player, event.getBow());
    }
}
