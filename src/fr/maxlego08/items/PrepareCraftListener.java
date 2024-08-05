package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.configurations.RecipeConfiguration;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PrepareCraftListener implements Listener {

    private final ItemManager itemManager;

    public PrepareCraftListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        ItemStack result = recipe.getResult();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;

        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;
        Item itemResult = itemOptional.get();

        RecipeConfiguration recipeConfiguration = itemResult.getConfiguration().getRecipeConfiguration();
        Map<String,String> ingredientsString = recipeConfiguration.ingredients();
        String[] pattern = recipeConfiguration.pattern();

        //TODO : handle check if item in matrix must be a custom item

        event.getInventory().setResult(itemResult.build(player, result.getAmount()));
    }

}
