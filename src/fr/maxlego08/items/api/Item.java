package fr.maxlego08.items.api;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public interface Item {

    NamespacedKey ITEM_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(ItemsPlugin.class), "item-id");

    ItemConfiguration getConfiguration();

    String getName();

    ItemStack build(Player player, int amount);

}
