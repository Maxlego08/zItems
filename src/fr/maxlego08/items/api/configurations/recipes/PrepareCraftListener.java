package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrepareCraftListener implements Listener {

    private final ItemManager itemManager;

    public PrepareCraftListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @EventHandler
    public void onSmelt(BlockCookEvent event) {
        ItemStack item = event.getSource();
        if (item == null || item.isEmpty()) return;

        if(event.getRecipe() == null) {
            return;
        }

        ItemStack result = event.getRecipe().getResult();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;
        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;

        NamespacedKey key = event.getRecipe().getKey();
        List<ItemRecipe> itemRecipes = itemOptional.get().getConfiguration()
                .getRecipeConfiguration().recipes();
        for (ItemRecipe itemRecipe : itemRecipes) {
            if (!RecipeType.smeltingRecipes().contains(itemRecipe.recipeType())) continue;
            if (!itemRecipe.recipeType().getNamespacedKey(itemOptional.get().getName())
                    .equals(key))
                continue;

            if(!isSimilar(item, itemRecipe.ingredients()[0])) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onSmithingTransform(PrepareSmithingEvent event) {

        Player player = (Player) event.getView().getPlayer();
        ItemStack item = event.getResult();
        if (item == null || item.isEmpty()) return;

        ItemStack newResult = this.setCustomResultWithPlaceholders(item, player);

        ItemStack template = event.getInventory().getInputTemplate();
        ItemStack base = event.getInventory().getInputEquipment();
        ItemStack addition = event.getInventory().getInputMineral();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;
        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;

        List<ItemRecipe> itemRecipes = itemOptional.get().getConfiguration()
                .getRecipeConfiguration().recipes();
        for (ItemRecipe itemRecipe : itemRecipes) {
            if (itemRecipe.recipeType() != RecipeType.SMITHING_TRANSFORM) continue;
            if (!itemRecipe.recipeType().getNamespacedKey(itemOptional.get().getName())
                    .equals(((SmithingRecipe) event.getInventory().getRecipe()).getKey()))
                continue;
            ItemRecipe.Ingredient templateIngredient = itemRecipe.ingredients()[0];
            ItemRecipe.Ingredient baseIngredient = itemRecipe.ingredients()[1];
            ItemRecipe.Ingredient additionIngredient = itemRecipe.ingredients()[2];

            boolean isSimilar = isSimilar(template, templateIngredient)
                    && isSimilar(base, baseIngredient)
                    && isSimilar(addition, additionIngredient);

            if(!isSimilar) {
                event.setResult(new ItemStack(Material.AIR));
                return;
            }
        }
        if (newResult != null) event.setResult(newResult);
    }

    private boolean isSimilar(ItemStack item, ItemRecipe.Ingredient itemIngredient) {
        if (itemIngredient.ingredientName().contains("zitems")) {
            if (!item.hasItemMeta()) {
                return false;
            }
            ItemMeta itemMeta = item.getItemMeta();
            PersistentDataContainer itemContainer = itemMeta.getPersistentDataContainer();
            if (!itemContainer.has(Item.ITEM_KEY, PersistentDataType.STRING)) {
                return false;
            }
            if (!itemContainer.get(Item.ITEM_KEY, PersistentDataType.STRING).
                    equals(itemIngredient.ingredientName().split(":")[1])) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;

        ItemStack result = recipe.getResult();
        ItemStack newResult = this.setCustomResultWithPlaceholders(result, player);

        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(Item.ITEM_KEY, PersistentDataType.STRING)) return;
        Optional<Item> itemOptional = itemManager.getItem(container.get(Item.ITEM_KEY, PersistentDataType.STRING));
        if (itemOptional.isEmpty()) return;

        var itemRecipes = itemOptional.get().getConfiguration()
                .getRecipeConfiguration().recipes();
        for (ItemRecipe itemRecipe : itemRecipes) {
            if(recipe instanceof ShapedRecipe shapedRecipe && itemRecipe.recipeType() == RecipeType.CRAFTING_SHAPED) {
                if (!shapedRecipe.getKey().equals(itemRecipe.recipeType().getNamespacedKey(itemOptional.get().getName()))) continue;
                AtomicBoolean isSimilar = new AtomicBoolean(true);
                ItemStack[] matrix = event.getInventory().getMatrix();
                for (int i = 0; i < matrix.length; i++) {
                    ItemStack stack = matrix[i];
                    if (stack == null || stack.getType() == Material.AIR) continue;
                    int row = i / 3;
                    int column = i % 3;
                    String[] pattern = itemRecipe.pattern();
                    char sign = pattern[row].split("")[column].toCharArray()[0];
                    Arrays.stream(itemRecipe.ingredients()).filter(ingredient -> ingredient.sign() == sign).findFirst().ifPresent(ingredient -> {
                        if(ingredient.ingredientName().contains("zitems")) {
                            if (!stack.hasItemMeta()) {
                                isSimilar.set(false);
                                return;
                            }
                            ItemMeta metaStack = stack.getItemMeta();
                            PersistentDataContainer containerStack = metaStack.getPersistentDataContainer();
                            if (!containerStack.has(Item.ITEM_KEY, PersistentDataType.STRING)) {
                                isSimilar.set(false);
                                return;
                            }
                            if(!containerStack.get(Item.ITEM_KEY, PersistentDataType.STRING).equals(ingredient.ingredientName().split(":")[1])) {
                                isSimilar.set(false);
                            }
                        }
                    });
                    if(!isSimilar.get()) {
                        event.getInventory().setResult(new ItemStack(Material.AIR));
                        return;
                    }
                }
            }

            if(recipe instanceof ShapelessRecipe shapelessRecipe && itemRecipe.recipeType() == RecipeType.CRAFTING_SHAPELESS) {
                if(!shapelessRecipe.getKey().equals(itemRecipe.recipeType().getNamespacedKey(itemOptional.get().getName()))) continue;

                List<ItemStack> matrix = Arrays.stream(event.getInventory().getMatrix()).filter(Objects::nonNull).toList();
                ItemRecipe.Ingredient[] itemIngredients = itemRecipe.ingredients();

                AtomicBoolean isSimilar = new AtomicBoolean(true);
                for (ItemRecipe.Ingredient ingredient : itemIngredients) {
                    boolean found = matrix.stream().anyMatch(stack -> {
                        if (stack == null || stack.getType() == Material.AIR) return false;
                        if (ingredient.ingredientName().contains("zitems")) {
                            if (!stack.hasItemMeta()) return false;
                            ItemMeta metaStack = stack.getItemMeta();
                            PersistentDataContainer containerStack = metaStack.getPersistentDataContainer();
                            return containerStack.has(Item.ITEM_KEY, PersistentDataType.STRING) &&
                                    containerStack.get(Item.ITEM_KEY, PersistentDataType.STRING).equals(ingredient.ingredientName().split(":")[1]);
                        } else {
                            return ((RecipeChoice.MaterialChoice) ingredient.choice()).getChoices().contains(stack.getType());
                        }
                    });
                    if (!found) {
                        isSimilar.set(false);
                        break;
                    }
                }

                if (!isSimilar.get()) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }
        }

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
