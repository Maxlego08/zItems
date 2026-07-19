package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Extractor for PlayerItemConsumeEvent.
 * <p>
 * Extracts the item actually being consumed (main hand or offhand), rather than
 * assuming the main hand like the generic {@link PlayerEventExtractor} fallback would.
 */
@AutoExtractor(PlayerItemConsumeEvent.class)
public class PlayerItemConsumeExtractor implements ItemSourceExtractor<PlayerItemConsumeEvent> {

    @Override
    public ExtractionResult extract(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, event.getItem());
    }
}
