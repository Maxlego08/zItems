package fr.maxlego08.items.api.configurations;

import fr.maxlego08.items.api.FakeItem;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public record BlockStateMetaConfiguration(boolean enable, List<ItemSlot> containerItems) {

    public static BlockStateMetaConfiguration loadBlockStateMeta(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableBlockStateMeta = configuration.getBoolean("block-state-meta.enable", false);
        List<ItemSlot> containerItems = new ArrayList<>();

        if (enableBlockStateMeta) {
            try {

                ConfigurationSection configurationSection = configuration.getConfigurationSection(path + "block-state-meta.container");
                if (configurationSection != null) {
                    configurationSection.getKeys(false).forEach(key -> {

                        String currentPath = path + "block-state-meta.container." + key + ".";
                        String customItem = configuration.getString(currentPath + "custom-item");
                        if (customItem != null) {
                            if (fileName.contains(customItem)) {
                                plugin.getLogger().severe("You cannot define the item itself in a container, this will create an endless loop !");
                            } else {
                                containerItems.add(new ItemSlotCustomItem(plugin, Integer.parseInt(key), customItem, configuration.getInt(currentPath + "amount", 0)));
                            }
                        } else {
                            containerItems.add(new ItemSlotItem(Integer.parseInt(key), plugin.createItem(key, new ItemConfiguration(plugin, configuration, fileName, currentPath))));
                        }
                    });
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
            this.containerItems.forEach(itemSlot -> inventory.setItem(itemSlot.slot(), itemSlot.item(player).build(player, itemSlot.amount())));
        }

        blockStateMeta.setBlockState(blockState);
    }

    public interface ItemSlot {

        int slot();

        Item item(Player player);

        int amount();

    }

    public record ItemSlotItem(int slot, Item item) implements ItemSlot {
        @Override
        public Item item(Player player) {
            return this.item;
        }

        @Override
        public int amount() {
            return 0;
        }
    }

    public static class ItemSlotCustomItem implements ItemSlot {
        private final ItemPlugin plugin;
        private final int slot;
        private final String customItem;
        private final int amount;

        public ItemSlotCustomItem(ItemPlugin plugin, int slot, String customItem, int amount) {
            this.plugin = plugin;
            this.slot = slot;
            this.customItem = customItem;
            this.amount = amount;
        }

        @Override
        public int slot() {
            return this.slot;
        }

        @Override
        public Item item(Player player) {
            var optional = this.plugin.getItemManager().getItem(customItem);
            return optional.orElse(new FakeItem());
        }

        @Override
        public int amount() {
            return this.amount;
        }
    }

}