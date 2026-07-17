package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;

/**
 * Extractor for EntityPickupItemEvent.
 * <p>
 * Not a {@code PlayerEvent} — extracts the picked-up item only when a player
 * is the one picking it up.
 */
@AutoExtractor(EntityPickupItemEvent.class)
public class EntityPickupItemExtractor implements ItemSourceExtractor<EntityPickupItemEvent> {

    @Override
    public ExtractionResult extract(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return null;
        }
        return new ExtractionResult(player, event.getItem().getItemStack());
    }
}
