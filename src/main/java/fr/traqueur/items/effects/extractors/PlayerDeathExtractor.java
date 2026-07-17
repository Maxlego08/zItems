package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Extractor for PlayerDeathEvent.
 * <p>
 * {@code PlayerDeathEvent} extends {@code EntityDeathEvent}, which already has
 * its own extractor ({@link EntityDeathExtractor}) resolving to the *killer's*
 * weapon — the right choice for a {@code KILL} entry. Registering this exact
 * match overrides that fallback for the dying player specifically, resolving
 * instead to whatever *they* were holding when they died.
 */
@AutoExtractor(PlayerDeathEvent.class)
public class PlayerDeathExtractor implements ItemSourceExtractor<PlayerDeathEvent> {

    @Override
    public ExtractionResult extract(PlayerDeathEvent event) {
        Player player = event.getEntity();
        return new ExtractionResult(player, player.getInventory().getItemInMainHand());
    }
}
