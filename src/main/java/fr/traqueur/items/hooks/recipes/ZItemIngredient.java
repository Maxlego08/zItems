package fr.traqueur.items.hooks.recipes;

import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.recipes.api.domains.Ingredient;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;

public class ZItemIngredient extends Ingredient {

    private final String id;
    private Item item;

    public ZItemIngredient(String id, Character sign) {
        super(sign);
        this.id = id;
    }

    @Override
    public boolean isSimilar(ItemStack item) {
        if (item == null) return false;
        if (!item.hasItemMeta()) return false;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return Keys.ITEM_ID.get(container).map(id -> this.item.id().equals(id)).orElse(false);
    }

    @Override
    public RecipeChoice choice() {
        if (this.item == null) {
            this.item = Registry.get(ItemsRegistry.class).getById(id);
        }
        Material material = this.item.settings().baseItem().material();
        if (material == null) {
            // Item uses copy-from (e.g. ItemsAdder), resolve the actual ItemStack
            ItemStack built = this.item.settings().baseItem().build(null);
            material = built.getType();
        }
        return new RecipeChoice.MaterialChoice(material);
    }
}
