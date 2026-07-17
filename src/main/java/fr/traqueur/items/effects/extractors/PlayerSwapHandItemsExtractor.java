package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Extractor for PlayerSwapHandItemsEvent.
 * <p>
 * Extracts the item ending up in the main hand after the swap.
 */
@AutoExtractor(PlayerSwapHandItemsEvent.class)
public class PlayerSwapHandItemsExtractor implements ItemSourceExtractor<PlayerSwapHandItemsEvent> {

    @Override
    public ExtractionResult extract(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getMainHandItem());
    }
}
