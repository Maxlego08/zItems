package fr.traqueur.items.effects.extractors;

import fr.traqueur.items.api.annotations.AutoExtractor;
import fr.traqueur.items.api.effects.ItemSourceExtractor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * Extractor for ProjectileHitEvent.
 * <p>
 * Not a {@code PlayerEvent} — traces the projectile back to its shooter, only
 * when a player fired it. Approximates the item used as whatever is still in
 * that player's main hand at hit time (the bow/crossbow itself isn't attached
 * to the projectile).
 */
@AutoExtractor(ProjectileHitEvent.class)
public class ProjectileHitExtractor implements ItemSourceExtractor<ProjectileHitEvent> {

    @Override
    public ExtractionResult extract(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return null;
        }
        return new ExtractionResult(player, player.getInventory().getItemInMainHand());
    }
}
