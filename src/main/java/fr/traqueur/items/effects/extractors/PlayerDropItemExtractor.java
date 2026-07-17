package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * Extractor for PlayerDropItemEvent.
 * <p>
 * Extracts the dropped item itself, which is not necessarily what's in the
 * player's main hand at the time of dropping.
 */
@AutoExtractor(PlayerDropItemEvent.class)
public class PlayerDropItemExtractor implements ItemSourceExtractor<PlayerDropItemEvent> {

    @Override
    public ExtractionResult extract(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getItemDrop().getItemStack());
    }
}
