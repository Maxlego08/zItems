package fr.traqueur.items.effects.entries;

import fr.traqueur.structura.api.Loadable;
import org.bukkit.entity.Entity;

/**
 * Wraps a single entity reference — a plain vanilla name ({@code "PIG"}) or a
 * {@code provider:id} custom mob reference ({@code "mythicmobs:my_boss"}), resolved
 * through {@code CustomEntityProviderRegistry}. Mirrors {@code IngredientWrapper}'s
 * shape: a dedicated settings object per reference rather than a bare string.
 *
 * @param entity the entity reference
 */
public record EntityMatch(String entity) implements Loadable {

    public boolean matches(Entity target) {
        return CustomMatch.entity(entity, target);
    }
}
