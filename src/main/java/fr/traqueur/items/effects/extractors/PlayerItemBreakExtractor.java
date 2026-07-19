package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemBreakEvent;

/**
 * Extractor for PlayerItemBreakEvent.
 * <p>
 * Extracts the item that just broke (durability reached zero) — already
 * removed from the player's inventory by the time this fires.
 */
@AutoExtractor(PlayerItemBreakEvent.class)
public class PlayerItemBreakExtractor implements ItemSourceExtractor<PlayerItemBreakEvent> {

    @Override
    public ExtractionResult extract(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getBrokenItem());
    }
}
