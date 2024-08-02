package fr.maxlego08.items.api;

import fr.maxlego08.items.api.configurations.ItemConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Item {

    ItemConfiguration getConfiguration();

    String getName();

    ItemStack build(Player player, int amount);

}
