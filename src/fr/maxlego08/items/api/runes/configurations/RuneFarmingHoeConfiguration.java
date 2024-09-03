package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Objects;

public class RuneFarmingHoeConfiguration extends RuneConfiguration {

    private final int size;
    private final boolean autoReplant;
    private final DropItemType dropItemType;
    private final boolean dropItemInInventory;
    private final boolean harvest;
    private final boolean plantSeeds;
    private final List<Material> blacklistMaterials;
    private final List<Material> allowedCrops;
    private final int damage;
    private final int harvestDamage;
    private final List<Material> allowedPlantSeeds;

    public RuneFarmingHoeConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        RuneFarmingHoeConfiguration.DropItemType dropItemType = RuneFarmingHoeConfiguration.DropItemType.CENTER;

        int size = configuration.getInt("size", 1);
        int damage = configuration.getInt("damage", 1);
        int harvestDamage = configuration.getInt("harvest-damage", 1);

        boolean autoReplant = configuration.getBoolean("auto-replant", true);
        boolean dropItemInInventory = configuration.getBoolean("add-item-in-inventory", false);
        boolean harvest = configuration.getBoolean("harvest", false);
        boolean plantSeeds = configuration.getBoolean("plant-seeds", false);

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

        this.size = size;
        this.allowedCrops = allowedCrops;
        this.autoReplant = autoReplant;
        this.blacklistMaterials = blacklistMaterials;
        this.damage = damage;
        this.dropItemInInventory = dropItemInInventory;
        this.dropItemType = dropItemType;
        this.harvest = harvest;
        this.plantSeeds = plantSeeds;
        this.harvestDamage = harvestDamage;
        this.allowedPlantSeeds = allowedPlantSeeds;
        this.eventBlockBreakEvent = this.loadEventBlockBreakEvent("enable-block-break-event");
    }

    private List<Material> stringListToMaterialList(List<String> strings) {
        return strings.stream().map(value -> {
            try {
                return Material.valueOf(value.toUpperCase());
            } catch (Exception ignored) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public int size() {
        return size;
    }

    public boolean autoReplant() {
        return autoReplant;
    }

    public DropItemType dropItemType() {
        return dropItemType;
    }

    public boolean dropItemInInventory() {
        return dropItemInInventory;
    }

    public boolean harvest() {
        return harvest;
    }

    public boolean plantSeeds() {
        return plantSeeds;
    }

    public List<Material> blacklistMaterials() {
        return blacklistMaterials;
    }

    public List<Material> allowedCrops() {
        return allowedCrops;
    }

    public int damage() {
        return damage;
    }

    public int harvestDamage() {
        return harvestDamage;
    }

    public List<Material> allowedPlantSeeds() {
        return allowedPlantSeeds;
    }

    public enum DropItemType {
        BLOCK,
        CENTER,
        PLAYER
    }
}
