package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;

/**
 * Extractor for PlayerItemDamageEvent.
 * <p>
 * Extracts the specific item taking durability damage, which may be a piece
 * of armor rather than whatever is in the main hand.
 */
@AutoExtractor(PlayerItemDamageEvent.class)
public class PlayerItemDamageExtractor implements ItemSourceExtractor<PlayerItemDamageEvent> {

    @Override
    public ExtractionResult extract(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getItem());
    }
}
