package fr.maxlego08.items.api.configurations.meta;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.utils.TagRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.components.ToolComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ToolComponentConfiguration(boolean enable, int damagePerBlock, float defaultMiningSpeed,
                                         List<ToolRuleTag> toolRuleTags, List<ToolMaterialTag> toolMaterialsTags) {

    public static ToolComponentConfiguration loadToolComponent(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {

        boolean enable = configuration.getBoolean(path + "tool-component.enable", false);
        if (enable) {
            try {

                int damagePerBlock = configuration.getInt(path + "tool-component.damage-per-block", 1);
                float defaultMiningSpeed = (float) configuration.getDouble(path + "tool-component.default-mining-speed", 1.0);
                List<ToolRuleTag> toolRuleTags = new ArrayList<>();
                List<ToolMaterialTag> toolMaterialTags = new ArrayList<>();
                for (Map<?, ?> map : configuration.getMapList(path + "tool-component.rules.tags")) {

                    if (map.containsKey("tag") && map.containsKey("speed") && map.containsKey("correct-for-drops")) {

                        String tagName = ((String) map.get("tag")).toUpperCase();
                        var tag = TagRegistry.getTags(tagName);
                        if (tag == null) {
                            plugin.getLogger().severe("Invalid tag " + tagName + " for item " + fileName + " (Map: " + map + ")");
                            continue;
                        }

                        float speed = ((Number) map.get("speed")).floatValue();
                        boolean correctForDrops = (boolean) map.get("correct-for-drops");

                        toolRuleTags.add(new ToolRuleTag(tag, speed, correctForDrops));

                    } else {
                        plugin.getLogger().severe("Invalid tags rules for item " + fileName + " (Map: " + map + ")");
                    }
                }

                for (Map<?, ?> map : configuration.getMapList(path + "tool-component.rules.materials")) {

                    if (map.containsKey("materials") && map.containsKey("speed") && map.containsKey("correct-for-drops")) {

                        List<Material> materials = ((List<?>) map.get("materials")).stream().map(e -> {
                            try {
                                return Material.valueOf(((String) e).toUpperCase());
                            } catch (Exception ignored) {
                                return null;
                            }
                        }).filter(Objects::nonNull).toList();
                        float speed = ((Number) map.get("speed")).floatValue();
                        boolean correctForDrops = (boolean) map.get("correct-for-drops");

                        toolMaterialTags.add(new ToolMaterialTag(materials, speed, correctForDrops));

                    } else {
                        plugin.getLogger().severe("Invalid tags rules for item " + fileName + " (Map: " + map + ")");
                    }
                }

                return new ToolComponentConfiguration(true, damagePerBlock, defaultMiningSpeed, toolRuleTags, toolMaterialTags);
            } catch (Exception exception) {
                plugin.getLogger().severe("Invalid tool component configuration in " + fileName);
                exception.printStackTrace();
            }
        }
        return new ToolComponentConfiguration(false, 0, 0f, null, null);
    }

    public void apply(ToolComponent toolComponent) {

        toolComponent.setDamagePerBlock(this.damagePerBlock);
        toolComponent.setDefaultMiningSpeed(this.defaultMiningSpeed);
        this.toolRuleTags.forEach(toolRuleTag -> toolComponent.addRule(toolRuleTag.tag(), toolRuleTag.speed(), toolRuleTag.correctForDrops()));
        this.toolMaterialsTags.forEach(toolRuleTag -> toolComponent.addRule(toolRuleTag.materials(), toolRuleTag.speed(), toolRuleTag.correctForDrops()));
    }
}
