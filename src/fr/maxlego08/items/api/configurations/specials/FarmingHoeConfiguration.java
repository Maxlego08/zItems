package fr.maxlego08.items.api.configurations.specials;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;

public record FarmingHoeConfiguration(int size,
                                      boolean autoReplant,
                                      DropItemType dropItemType,
                                      boolean dropItemInInventory,
                                      boolean harvest,
                                      boolean plantSeeds,
                                      List<Material> blacklistMaterials,
                                      List<Material> allowedCrops,
                                      int damage,
                                      int harvestDamage,
                                      List<Material> allowedPlantSeeds
) implements SpecialConfiguration {


    @Override
    public void apply(Player player, ItemPlugin plugin, ItemMeta itemMeta, PersistentDataContainer persistentDataContainer) {


    }

    public enum DropItemType {
        BLOCK,
        CENTER,
        PLAYER
    }
}
