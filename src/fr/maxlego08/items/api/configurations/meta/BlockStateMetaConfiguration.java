package fr.maxlego08.items.api.configurations.meta;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.configurations.state.ItemSlot;
import fr.maxlego08.items.api.configurations.state.ItemSlotCustomItem;
import fr.maxlego08.items.api.configurations.state.ItemSlotItem;
import fr.maxlego08.items.api.configurations.state.SignConfiguration;
import fr.maxlego08.items.api.configurations.state.SignLine;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public record BlockStateMetaConfiguration(boolean enable, List<ItemSlot> containerItems, boolean signWaxed,
                                          SignConfiguration frontSign, SignConfiguration backSign) {

    public static BlockStateMetaConfiguration loadBlockStateMeta(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableBlockStateMeta = configuration.getBoolean(path + "block-state-meta.enable", false);
        List<ItemSlot> containerItems = new ArrayList<>();
        SignConfiguration frontSign = null;
        SignConfiguration backSign = null;
        boolean signWaxed = false;

        if (enableBlockStateMeta) {
            try {

                containerItems = loadContainer(plugin, configuration, fileName, path);
                frontSign = loadSignConfiguration(plugin, configuration, fileName, path + "block-state-meta.sign.FRONT.");
                backSign = loadSignConfiguration(plugin, configuration, fileName, path + "block-state-meta.sign.BACK.");
                signWaxed = configuration.getBoolean(path + "block-state-meta.sign.waxed");

            } catch (Exception ignored) {
                plugin.getLogger().severe("Invalid block state configuration in " + fileName);
                enableBlockStateMeta = false;
            }
            return new BlockStateMetaConfiguration(enableBlockStateMeta, containerItems, signWaxed, frontSign, backSign);
        } else {
            return new BlockStateMetaConfiguration(false, null, false, null, null);
        }
    }

    private static SignConfiguration loadSignConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean glow = configuration.getBoolean(path + "glow", false);
        List<SignLine> lines = configuration.getMapList(path + "lines").stream().map(map -> {
            int index = ((Number) map.get("index")).intValue();
            String line = (String) map.get("line");
            return new SignLine(index, line);
        }).toList();
        return new SignConfiguration(glow, lines);
    }

    private static List<ItemSlot> loadContainer(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        List<ItemSlot> containerItems = new ArrayList<>();
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
        return containerItems;
    }

    public void apply(BlockStateMeta blockStateMeta, Player player, ItemComponent itemComponent) {
        BlockState blockState = blockStateMeta.getBlockState();

        if (blockState instanceof Container container && !this.containerItems.isEmpty()) {

            var inventory = container.getInventory();
            this.containerItems.forEach(itemSlot -> inventory.setItem(itemSlot.slot(), itemSlot.item(player).build(player, itemSlot.amount())));
        }

        if (blockState instanceof Sign sign) {

            sign.setWaxed(this.signWaxed);

            if (this.frontSign != null) {
                SignSide signSide = sign.getSide(Side.FRONT);
                signSide.setGlowingText(this.frontSign.glow());
                this.frontSign.lines().forEach(line -> itemComponent.setLine(signSide, line.index(), line.line()));
            }

            if (this.backSign != null) {
                SignSide signSide = sign.getSide(Side.BACK);
                signSide.setGlowingText(this.backSign.glow());
                this.backSign.lines().forEach(line -> itemComponent.setLine(signSide, line.index(), line.line()));
            }
        }

        blockStateMeta.setBlockState(blockState);
    }

}