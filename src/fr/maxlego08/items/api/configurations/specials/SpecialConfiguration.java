package fr.maxlego08.items.api.configurations.specials;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public interface SpecialConfiguration {


    void apply(Player player, ItemPlugin plugin, ItemMeta itemMeta, PersistentDataContainer persistentDataContainer);

}
