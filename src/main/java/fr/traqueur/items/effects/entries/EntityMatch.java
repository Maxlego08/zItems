package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.registries.CustomEntityProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * References a single entity — either vanilla ({@code entity-id: "PIG"}) or a custom
 * mob from a hook plugin ({@code plugin-name: "mythicmobs"}, {@code entity-id: "my_boss"}),
 * resolved through {@code CustomEntityProviderRegistry}. Mirrors {@code CopyFrom}'s
 * {@code plugin-name}/{@code item-id} shape.
 *
 * @param pluginName optional provider key (e.g. {@code "mythicmobs"}); absent means vanilla
 * @param entityId   the vanilla {@code EntityType} name, or the custom mob id within {@code pluginName}'s system
 */
public record EntityMatch(
        @Options(optional = true) String pluginName,
        String entityId
) implements Loadable {

    public boolean matches(Entity entity) {
        if (pluginName == null || pluginName.isBlank()) {
            try {
                return entity.getType() == EntityType.valueOf(entityId.toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return Registry.get(CustomEntityProviderRegistry.class).matches(pluginName.toLowerCase(), entityId, entity);
    }
}
