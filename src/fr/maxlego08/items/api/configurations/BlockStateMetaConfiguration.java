package fr.maxlego08.items.api.configurations;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public record BlockStateMetaConfiguration(boolean enable, List<ItemSlot> containerItems) {

    public static BlockStateMetaConfiguration loadBlockStateMeta(ItemsPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableBlockStateMeta = configuration.getBoolean("block-state-meta.enable", false);
        List<ItemSlot> containerItems = new ArrayList<>();

        if (enableBlockStateMeta) {
            try {

                ConfigurationSection configurationSection = configuration.getConfigurationSection(path + "block-state-meta.container");
                if (configurationSection != null) {
                    configurationSection.getKeys(false).forEach(key -> containerItems.add(new ItemSlot(Integer.parseInt(key), plugin.createItem(key, new ItemConfiguration(plugin, configuration, fileName, path + "block-state-meta.container." + key + ".")))));
                }

            } catch (Exception ignored) {
                plugin.getLogger().severe("Invalid block state configuration in " + fileName);
                enableBlockStateMeta = false;
            }
            return new BlockStateMetaConfiguration(enableBlockStateMeta, containerItems);
        } else {
            return new BlockStateMetaConfiguration(false, null);
        }
    }

    public void apply(BlockStateMeta blockStateMeta, Player player) {
        BlockState blockState = blockStateMeta.getBlockState();

        if (blockState instanceof Container container && !this.containerItems.isEmpty()) {

            var inventory = container.getInventory();
            this.containerItems.forEach(itemSlot -> inventory.setItem(itemSlot.slot, itemSlot.item.build(player, 1)));
        }

        blockStateMeta.setBlockState(blockState);
    }

    public record ItemSlot(int slot, Item item) {
    }

}