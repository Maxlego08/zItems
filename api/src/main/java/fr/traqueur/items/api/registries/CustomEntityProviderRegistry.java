package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.entities.CustomEntityProvider;
import org.bukkit.entity.Entity;

import java.util.Optional;

/**
 * Registry for managing custom entity (mob) providers from various sources.
 * <p>
 * Mirrors {@link CustomBlockProviderRegistry}, but for entities. Providers are keyed by
 * a lowercase source name (e.g. {@code "mythicmobs"}), matched against the
 * {@code provider:id} convention already used for custom blocks/ingredients
 * (see {@code IngredientWrapper}).
 */
public interface CustomEntityProviderRegistry extends Registry<String, CustomEntityProvider> {

    /**
     * Checks whether the given entity is a custom mob registered under {@code provider},
     * with the given custom id.
     *
     * @param provider the provider key (e.g. {@code "mythicmobs"})
     * @param id       the custom mob id to match
     * @param entity   the entity to check
     * @return true if the provider is registered and recognizes this entity as {@code id}
     */
    default boolean matches(String provider, String id, Entity entity) {
        CustomEntityProvider customEntityProvider = getById(provider);
        if (customEntityProvider == null) {
            return false;
        }
        Optional<String> customId = customEntityProvider.getCustomEntityId(entity);
        return customId.isPresent() && customId.get().equalsIgnoreCase(id);
    }
}
