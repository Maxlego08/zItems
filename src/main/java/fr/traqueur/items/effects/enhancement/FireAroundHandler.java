package fr.traqueur.items.effects.enhancement;

import fr.traqueur.items.api.annotations.AutoEffect;
import fr.traqueur.items.api.effects.EffectContext;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.effects.enhancement.FireAroundSettings;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Purely cosmetic ring of flame particles around the executor — demonstrates a
 * {@code SNEAKING}-gated pipeline step. Deliberately doesn't place real fire blocks:
 * those spread and persist, which is a bad default for something meant to fire
 * repeatedly every tick interval while a player sneaks.
 */
@AutoEffect("FIRE_AROUND")
public class FireAroundHandler implements EffectHandler.AnyEventEffectHandler<FireAroundSettings> {

    @Override
    public void handle(EffectContext context, FireAroundSettings settings) {
        Player player = context.executor();
        Location center = player.getLocation();
        World world = player.getWorld();

        int points = settings.points();
        double radius = settings.radius();

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            world.spawnParticle(Particle.FLAME, x, center.getY() + 0.1, z, 1);
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
