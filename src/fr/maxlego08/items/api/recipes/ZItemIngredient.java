package fr.maxlego08.items.api.recipes;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.traqueur.recipes.api.domains.BaseIngredient;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ZItemIngredient extends BaseIngredient {

    private final Item item;

    public ZItemIngredient(String id, Character sign) {
        super(sign);
        ItemManager itemManager = Bukkit.getServer().getServicesManager().getRegistration(ItemManager.class).getProvider();
        this.item = itemManager.getItem(id).orElseThrow();
    }

    @Override
    public boolean isSimilar(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return false;
        String id = container.get(Item.ITEM_KEY, PersistentDataType.STRING);
        return id.equals(item.getName());
    }

    @Override
    public RecipeChoice choice() {
        return new RecipeChoice.MaterialChoice(item.getConfiguration().getMaterial());
    }
}
