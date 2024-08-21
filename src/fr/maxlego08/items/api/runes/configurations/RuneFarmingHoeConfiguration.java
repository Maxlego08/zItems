package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.ItemsPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record RuneFarmingHoeConfiguration(int size,
                                          boolean autoReplant,
                                          DropItemType dropItemType,
                                          boolean dropItemInInventory,
                                          boolean harvest,
                                          boolean plantSeeds,
                                          List<Material> blacklistMaterials,
                                          List<Material> allowedCrops,
                                          int damage,
                                          int harvestDamage,
                                          List<Material> allowedPlantSeeds,
                                          boolean eventBlockBreakEvent
) implements RuneConfiguration {


    public static RuneConfiguration loadConfiguration(ItemsPlugin plugin, YamlConfiguration configuration, String runeName) {

        RuneFarmingHoeConfiguration.DropItemType dropItemType = RuneFarmingHoeConfiguration.DropItemType.CENTER;

        int size = configuration.getInt("size", 1);
        int damage = configuration.getInt("damage", 1);
        int harvestDamage = configuration.getInt("harvest-damage", 1);

        boolean autoReplant = configuration.getBoolean("auto-replant", true);
        boolean dropItemInInventory = configuration.getBoolean("add-item-in-inventory", false);
        boolean harvest = configuration.getBoolean("harvest", false);
        boolean plantSeeds = configuration.getBoolean("plant-seeds", false);
        boolean eventBlockBreakEvent = configuration.getBoolean("enable-block-break-event", true);

        List<Material> blacklistMaterials = stringListToMaterialList(configuration.getStringList("drop-blacklist"));
        List<Material> allowedCrops = stringListToMaterialList(configuration.getStringList("allowed-crops"));
        List<Material> allowedPlantSeeds = stringListToMaterialList(configuration.getStringList("allowed-plant-seeds"));

        if (size % 2 == 0) {
            size = 3;
            plugin.getLogger().severe("Farming hoe size must be odd ! Use default value: 3. For rune " + runeName);
        }

        String dropItemTypeString = configuration.getString("drop-item-type");
        if (dropItemTypeString != null) {
            try {
                dropItemType = RuneFarmingHoeConfiguration.DropItemType.valueOf(dropItemTypeString.toUpperCase());
            } catch (Exception exception) {
                plugin.getLogger().severe("Impossible to find the drop-item-type " + dropItemTypeString + " For rune " + runeName);
            }
        }

        return new RuneFarmingHoeConfiguration(size, autoReplant, dropItemType, dropItemInInventory, harvest, plantSeeds, blacklistMaterials, allowedCrops, damage, harvestDamage, allowedPlantSeeds, eventBlockBreakEvent);
    }

    private static List<Material> stringListToMaterialList(List<String> strings) {
        return strings.stream().map(value -> {
            try {
                return Material.valueOf(value.toUpperCase());
            } catch (Exception ignored) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public enum DropItemType {
        BLOCK,
        CENTER,
        PLAYER
    }
}
