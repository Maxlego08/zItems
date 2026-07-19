package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;

/**
 * Extractor for PlayerItemHeldEvent.
 * <p>
 * Extracts the item about to become held (the new slot), not the one the
 * default {@code getItemInMainHand()} would still report at this point in the
 * event. There is no equivalent "just stopped being held" entry: a dispatch
 * only ever resolves one item per event, so only one direction of this event
 * can be observed.
 */
@AutoExtractor(PlayerItemHeldEvent.class)
public class PlayerItemHeldExtractor implements ItemSourceExtractor<PlayerItemHeldEvent> {

    @Override
    public ExtractionResult extract(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        return new ExtractionResult(player, player.getInventory().getItem(event.getNewSlot()));
    }
}
