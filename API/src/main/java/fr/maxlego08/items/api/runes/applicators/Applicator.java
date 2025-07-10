package fr.maxlego08.items.api.runes.applicators;

import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneManager;
import fr.traqueur.recipes.api.domains.Ingredient;
import fr.traqueur.recipes.impl.domains.ItemRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public record Applicator(ItemPlugin plugin, ItemRecipe recipe, Rune rune, Material baseItem, int nbInputs, int nbExtraInputs) {

    public boolean canApply(ItemStack baseItem, ItemStack runeItem, List<ItemStack> inputItems, List<ItemStack> extraInputItems) {
        RuneManager runeManager = plugin.getRuneManager();
        ItemManager itemManager = plugin.getItemManager();

        if(baseItem == null || baseItem.getType() != this.baseItem) {
            return false;
        }

        var runes = runeManager.getRunes(baseItem);
        AtomicBoolean canApply = new AtomicBoolean(true);
        runes.ifPresent(runeList -> {
            if (runeList
                    .stream()
                    .anyMatch(rune -> rune.getName().equals(this.rune.getName()))) {
                canApply.set(false);
            }
        });
        if (!canApply.get()) {
            return false;
        }

        itemManager.getItem(runeItem).ifPresentOrElse(item -> {
            if (item.getConfiguration().getItemType() != ItemType.RUNE) {
                canApply.set(false);
                return;
            }
            Rune rune = item.getConfiguration().getItemRuneConfiguration().rune();
            if (!rune.getName().equals(this.rune.getName())) {
                canApply.set(false);
            }
        }, () -> canApply.set(false));

        if (!canApply.get()) {
            return false;
        }

        if(inputItems.size() != this.nbInputs || extraInputItems.size() != this.nbExtraInputs) {
            return false;
        }

        List<Ingredient> recipeIngredients = new ArrayList<>(Arrays.asList(this.recipe.ingredients()));
        new ArrayList<>(recipeIngredients).stream()
                .filter(ingredient -> ingredient.isSimilar(runeItem) || ingredient.isSimilar(baseItem))
                .forEach(recipeIngredients::remove);

        if (!testIfInputsMatch(inputItems, new ArrayList<>(recipeIngredients.subList(0, nbInputs)))) return false;
        if (!testIfInputsMatch(extraInputItems, new ArrayList<>(recipeIngredients.subList(nbInputs, nbInputs + nbExtraInputs)))) return false;

        return this.recipe.ingredients()[this.recipe.ingredients().length - 1].isSimilar(baseItem);
    }

    private boolean testIfInputsMatch(List<ItemStack> inputItems, List<Ingredient> recipeIngredients) {
        List<Ingredient> recipeIngredientsFiltered = recipeIngredients.stream()
                .collect(Collectors.toList());
        List<ItemStack> inputItemsFiltered = inputItems.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (recipeIngredientsFiltered.size() != inputItemsFiltered.size()) {
            return false;
        }
        var iterator = recipeIngredientsFiltered.iterator();
        while (iterator.hasNext()) {
            Ingredient ingredient = iterator.next();
            boolean matched = inputItemsFiltered.stream()
                    .anyMatch(ingredient::isSimilar);

            if (!matched) {
                return false;
            }
            iterator.remove();
            inputItemsFiltered = inputItemsFiltered.stream()
                    .filter(inputItem -> !ingredient.isSimilar(inputItem))
                    .collect(Collectors.toList());
        }

        return recipeIngredientsFiltered.isEmpty();
    }
}
