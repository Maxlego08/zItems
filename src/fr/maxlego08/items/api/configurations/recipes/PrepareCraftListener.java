package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.*;
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
    public void onSmithingTransform(PrepareSmithingEvent event) {
        Player player = (Player) event.getView().getPlayer();
        ItemStack item = event.getResult();
        if(item == null || item.isEmpty()) return;

        ItemStack newResult = this.setCustomResultWithPlaceholders(item, player);
        if(newResult != null) event.getInventory().setResult(newResult);
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        ItemStack result = recipe.getResult();
        ItemStack newResult = this.setCustomResultWithPlaceholders(result, player);
        if(newResult != null) event.getInventory().setResult(newResult);
    }

    private ItemStack setCustomResultWithPlaceholders(ItemStack result, Player player) {
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return null;

        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return null;
        Item itemResult = itemOptional.get();
        return itemResult.build(player, result.getAmount());
    }
}
