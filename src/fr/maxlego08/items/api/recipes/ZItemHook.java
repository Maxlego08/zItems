package fr.maxlego08.items.api.recipes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.zcore.ZPlugin;
import fr.traqueur.recipes.api.domains.BaseIngredient;
import fr.traqueur.recipes.api.hook.Hook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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
    public BaseIngredient getIngredient(String s, Character character) {
        return new ZItemIngredient(s, character);
    }

    @Override
    public ItemStack getItemStack(String s) {
        return itemPlugin.getItemManager().getItem(s).orElseThrow().build(null, 1);
    }
}
