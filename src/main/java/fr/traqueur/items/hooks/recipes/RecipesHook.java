package fr.traqueur.items.hooks.recipes;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.recipes.api.domains.Ingredient;
import fr.traqueur.recipes.api.hook.Hook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record RecipesHook(ItemsPlugin plugin) implements Hook {

    @Override
    public String getPluginName() {
        return this.plugin.getName();
    }

    @Override
    public Ingredient getIngredient(String data, Character sign) {
        return new ZItemIngredient(data, sign);
    }

    @Override
    public ItemStack getItemStack(Player player, String resultPart) {
        return Registry.get(ItemsRegistry.class).getById(resultPart).build(player, 1);
    }
}
