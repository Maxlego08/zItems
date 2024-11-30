package fr.maxlego08.items.api.runes.applicators;

import fr.maxlego08.items.api.ItemManager;
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

public record Applicator(RuneManager runeManager, ItemRecipe recipe, Rune rune, Material baseItem, int nbInputs, int nbExtraInputs) {

    public boolean canApply(ItemStack baseItem, List<ItemStack> inputItems, List<ItemStack> extraInputItems) {
        var runes = runeManager.getRunes(baseItem);
        AtomicBoolean canApply = new AtomicBoolean(true);
        runes.ifPresent(runeList -> {
            if (runeList.stream().anyMatch(rune -> rune.getName().equals(this.rune.getName()))) {
                canApply.set(false);
            }
        });
        if (!canApply.get()) {
            return false;
        }

        if(baseItem == null || baseItem.getType() != this.baseItem) {
            return false;
        }

        if(inputItems.size() != this.nbInputs || extraInputItems.size() != this.nbExtraInputs) {
            return false;
        }

        List<Ingredient> recipeIngredients = new ArrayList<>(Arrays.asList(this.recipe.ingredients()));
        if (!testIfInputsMatch(inputItems, recipeIngredients.subList(0, nbInputs))) return false;
        if (!testIfInputsMatch(extraInputItems, recipeIngredients.subList(nbInputs, nbInputs + nbExtraInputs))) return false;

        if(!this.recipe.ingredients()[this.recipe.ingredients().length-1].isSimilar(baseItem)) {
            return false;
        }

        return true;
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
