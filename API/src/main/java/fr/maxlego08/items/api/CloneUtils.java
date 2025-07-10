package fr.maxlego08.items.api;

import fr.maxlego08.menu.api.dupe.DupeManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class CloneUtils {

    public static ItemStack cloneItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        ItemStack clone = itemStack.clone();
        ItemMeta cloneMeta = clone.getItemMeta();

        if (cloneMeta == null) {
            return clone;
        }
        PersistentDataContainer container = cloneMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(Bukkit.getServer().getPluginManager().getPlugin("zMenu"), DupeManager.KEY);
        if (container.has(key)) {
            container.remove(key);
        }
        clone.setItemMeta(cloneMeta);
        return clone;
    }

}
