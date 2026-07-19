package fr.traqueur.items.api.entities;

import org.bukkit.entity.Entity;

import java.util.Optional;

/**
 * Provider interface for detecting custom mobs from various sources (MythicMobs, etc.).
 * <p>
 * Mirrors {@link fr.traqueur.items.api.blocks.CustomBlockProvider}, but for entities.
 */
public interface CustomEntityProvider {

    /**
     * Attempts to get the custom mob type id for a given entity.
     *
     * @param entity the entity to check
     * @return Optional containing the custom mob type id, or empty if not a recognized custom mob
     */
    Optional<String> getCustomEntityId(Entity entity);
}
