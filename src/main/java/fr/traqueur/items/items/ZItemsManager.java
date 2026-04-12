package fr.traqueur.items.items;

import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.ItemsManager;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.recipes.api.RecipesAPI;
import fr.traqueur.recipes.impl.domains.ItemRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ZItemsManager implements ItemsManager {

    private final List<ItemRecipe> recipes;

    public ZItemsManager() {
        this.recipes = new ArrayList<>();
    }

    @Override
    public void generateRecipesFromLoadedItems() {
        RecipesAPI recipesAPI = this.getPlugin().getRecipesManager();
        for (ItemRecipe recipe : recipes) {
            recipesAPI.removeRecipe(recipe);
        }
        Collection<Item> items = Registry.get(ItemsRegistry.class).getAll();
        for (Item item : items) {
            if (item.recipe() != null) {
                ItemRecipe itemRecipe = item.recipe().build(this.getPlugin().getName(), item);
                this.recipes.add(itemRecipe);
                recipesAPI.addRecipe(itemRecipe);
            }
        }
        Logger.debug("Generated " + this.recipes.size() + " item recipes from loaded items.");
    }

    @Override
    public Optional<Item> getCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return Optional.empty();
        }

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }

        Optional<String> itemId = Keys.ITEM_ID.get(meta.getPersistentDataContainer());
        if (itemId.isEmpty()) {
            return Optional.empty();
        }

        ItemsRegistry registry = Registry.get(ItemsRegistry.class);
        if (registry == null) {
            return Optional.empty();
        }

        Item item = registry.getById(itemId.get());
        return Optional.ofNullable(item);
    }

}
