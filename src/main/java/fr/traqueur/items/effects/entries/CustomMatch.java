package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import fr.traqueur.items.api.registries.CustomEntityProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Parses a single match pattern as either a plain vanilla name ({@code "STONE"},
 * {@code "PIG"}) or a {@code provider:id} custom reference ({@code "itemsadder:ruby_ore"},
 * {@code "mythicmobs:my_boss"}) — the same convention already used by {@code IngredientWrapper}
 * ({@code "tag:planks"}, {@code "zitems:custom_item_id"}).
 */
public final class CustomMatch {

    private CustomMatch() {
    }

    public static boolean block(String pattern, Block block) {
        int separator = pattern.indexOf(':');
        if (separator < 0) {
            Material material = Material.matchMaterial(pattern);
            return material != null && block.getType() == material;
        }

        String provider = pattern.substring(0, separator).toLowerCase();
        String id = pattern.substring(separator + 1);
        var customBlockProvider = Registry.get(CustomBlockProviderRegistry.class).getById(provider);
        if (customBlockProvider == null) {
            return false;
        }
        return customBlockProvider.getCustomBlockId(block)
                .map(customId -> customId.equalsIgnoreCase(id))
                .orElse(false);
    }

    public static boolean entity(String pattern, Entity entity) {
        int separator = pattern.indexOf(':');
        if (separator < 0) {
            try {
                return entity.getType() == EntityType.valueOf(pattern.toUpperCase());
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        String provider = pattern.substring(0, separator).toLowerCase();
        String id = pattern.substring(separator + 1);
        return Registry.get(CustomEntityProviderRegistry.class).matches(provider, id, entity);
    }
}
