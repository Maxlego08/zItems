package fr.maxlego08.items.api;

import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.api.utils.TrimHelper;
import org.bukkit.plugin.Plugin;

public interface ItemPlugin extends Plugin  {

    Item createItem(String name, ItemConfiguration itemConfiguration);

    Enchantments getEnchantments();

    TrimHelper getTrimHelper();

    ItemManager getItemManager();
}
