package fr.maxlego08.items.api.recipes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.traqueur.recipes.api.domains.Ingredient;
import fr.traqueur.recipes.api.hook.Hook;
import org.bukkit.inventory.ItemStack;

public class ZItemHook implements Hook {

    private final ItemPlugin itemPlugin;

    public ZItemHook(ItemPlugin itemPlugin) {
        this.itemPlugin = itemPlugin;
    }

    @Override
    public String getPluginName() {
        return this.itemPlugin.getName();
    }

    @Override
    public Ingredient getIngredient(String s, Character character) {
        return new ZItemIngredient(s, character);
    }

    @Override
    public ItemStack getItemStack(String s) {
        return itemPlugin.getItemManager().getItem(s).orElseThrow().build(null, 1);
    }
}
