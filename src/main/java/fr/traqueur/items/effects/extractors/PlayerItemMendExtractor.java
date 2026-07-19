package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemMendEvent;

/**
 * Extractor for PlayerItemMendEvent.
 * <p>
 * Extracts the item being repaired by Mending, which may be a piece of armor
 * rather than whatever is in the main hand.
 */
@AutoExtractor(PlayerItemMendEvent.class)
public class PlayerItemMendExtractor implements ItemSourceExtractor<PlayerItemMendEvent> {

    @Override
    public ExtractionResult extract(PlayerItemMendEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getItem());
    }
}
