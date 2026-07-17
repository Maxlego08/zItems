package fr.traqueur.items.entities;

import fr.traqueur.items.api.entities.CustomEntityProvider;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.Optional;

/**
 * Detects MythicMobs-spawned entities without any compile dependency on MythicMobs
 * itself — MythicMobs tags its entities via Bukkit's standard {@code Metadatable} API
 * ({@code "MythicMobs"} / {@code "type"} metadata keys), which is part of the plugin's
 * own documented soft-integration convention. Simply returns empty when MythicMobs
 * isn't installed (no entity will ever carry that metadata), so this is always safe
 * to register unconditionally.
 */
public class MythicMobsProvider implements CustomEntityProvider {

    @Override
    public Optional<String> getCustomEntityId(Entity entity) {
        if (!entity.hasMetadata("MythicMobs")) {
            return Optional.empty();
        }
        List<MetadataValue> type = entity.getMetadata("type");
        if (type.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(type.get(0).asString());
    }
}
