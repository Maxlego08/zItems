package fr.maxlego08.items.api;

import fr.maxlego08.items.api.configurations.ItemConfiguration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FakeItem implements Item {
    @Override
    public ItemConfiguration getConfiguration() {
        return null;
    }

    @Override
    public String getName() {
        return "FakeItem";
    }

    @Override
    public ItemStack build(Player player, int amount) {
        return new ItemStack(Material.STONE);
    }
}
