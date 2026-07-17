package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.blocks.CustomBlockProvider;
import fr.traqueur.items.api.registries.CustomBlockProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * References a single block — either vanilla ({@code block-id: "STONE"}) or a custom
 * block from a hook plugin ({@code plugin-name: "itemsadder"}, {@code block-id: "ruby_ore"}),
 * resolved through {@code CustomBlockProviderRegistry}. Mirrors {@code CopyFrom}'s
 * {@code plugin-name}/{@code item-id} shape.
 *
 * @param pluginName optional provider key (e.g. {@code "itemsadder"}); absent means vanilla
 * @param blockId    the vanilla {@code Material} name, or the custom block id within {@code pluginName}'s system
 */
public record BlockMatch(
        @Options(optional = true) String pluginName,
        String blockId
) implements Loadable {

    public boolean matches(Block block) {
        if (pluginName == null || pluginName.isBlank()) {
            Material material = Material.matchMaterial(blockId);
            return material != null && block.getType() == material;
        }

        CustomBlockProvider provider = Registry.get(CustomBlockProviderRegistry.class).getById(pluginName.toLowerCase());
        if (provider == null) {
            return false;
        }
        return provider.getCustomBlockId(block)
                .map(customId -> customId.equalsIgnoreCase(blockId))
                .orElse(false);
    }
}
