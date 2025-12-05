package fr.traqueur.items.items.metadata;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.annotations.AutoMetadata;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Tool component metadata configuration for custom tools.
 * Allows configuring mining speeds, damage per block, and mining rules.
 *
 * <p>This metadata is only available on Paper servers.
 *
 * <p>Example YAML:
 * <pre>
 * metadata:
 *   tool:
 *     damage-per-block: 1
 *     default-mining-speed: 1.0
 *     rules:
 *       - tag: MINEABLE_PICKAXE
 *         speed: 8.0
 *         correct-for-drops: true
 *     material-rules:
 *       - materials:
 *           - STONE
 *           - COBBLESTONE
 *         speed: 10.0
 *         correct-for-drops: true
 * </pre>
 *
 * Discriminator key: "tool"
 */
@AutoMetadata("tool")
public record ToolMetadata(
        @Options(optional = true) @DefaultInt(1) int damagePerBlock,
        @Options(optional = true) @DefaultDouble(1.0) double defaultMiningSpeed,
        @Options(optional = true) List<ToolRule> rules,
        @Options(optional = true) List<ToolMaterialRule> materialRules
) implements ItemMetadata {

    @Override
    public void apply(ItemStack itemStack, @Nullable Player player) {
        try {
            boolean applied = ItemUtil.editMeta(itemStack, ItemMeta.class, meta -> {
                // Get the current tool component or create a new one
                ToolComponent toolComponent = meta.getTool();

                // Set damage per block
                toolComponent.setDamagePerBlock(damagePerBlock);

                // Set default mining speed
                toolComponent.setDefaultMiningSpeed((float) defaultMiningSpeed);

                // Add tag-based rules
                if (rules != null && !rules.isEmpty()) {
                    for (ToolRule rule : rules) {
                        toolComponent.addRule(rule.tag(), (float) rule.speed(), rule.correctForDrops());
                    }
                }

                // Add material-based rules
                if (materialRules != null && !materialRules.isEmpty()) {
                    for (ToolMaterialRule rule : materialRules) {
                        toolComponent.addRule(rule.materials(), (float) rule.speed(), rule.correctForDrops());
                    }
                }

                // Apply the modified tool component back to the meta
                meta.setTool(toolComponent);
            });

            if (!applied) {
                Logger.severe("Failed to apply ToolComponent to ItemStack of type {}", itemStack.getType().name());
            }

        } catch (Exception e) {
            Logger.severe("Failed to apply ToolComponent to ItemStack of type {}: {}",
                    e, itemStack.getType().name(), e.getMessage());
        }
    }

    /**
     * Represents a tool mining rule based on specific materials.
     * Defines mining speed and drop behavior for specific block types.
     *
     * <p>Example YAML:
     * <pre>
     * materials:
     *   - STONE
     *   - COBBLESTONE
     *   - GRANITE
     * speed: 10.0
     * correct-for-drops: true
     * </pre>
     */
    public record ToolMaterialRule(
            List<Material> materials,
            double speed,
            @Options(optional = true) @DefaultBool(true) boolean correctForDrops
    ) implements Loadable {
    }

    /**
     * Represents a tool mining rule based on block tags.
     * Defines mining speed and drop behavior for blocks matching a specific tag.
     *
     * <p>Example YAML:
     * <pre>
     * tag: MINEABLE_PICKAXE
     * speed: 8.0
     * correct-for-drops: true
     * </pre>
     */
    public record ToolRule(
            Tag<Material> tag,
            double speed,
            @Options(optional = true) @DefaultBool(true) boolean correctForDrops
    ) implements Loadable {
    }

}
