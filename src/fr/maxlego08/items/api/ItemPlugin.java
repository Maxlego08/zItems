package fr.maxlego08.items.api;

import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.api.hook.BlockAccess;
import fr.maxlego08.items.api.utils.TrimHelper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface ItemPlugin extends Plugin {

    Item createItem(String name, ItemConfiguration itemConfiguration);

    Enchantments getEnchantments();

    TrimHelper getTrimHelper();

    ItemManager getItemManager();

    List<BlockAccess> getBlockAccess();

    void registerBlockAccess(BlockAccess blockAccess);

    boolean hasAccess(Player player, Location location);
}
