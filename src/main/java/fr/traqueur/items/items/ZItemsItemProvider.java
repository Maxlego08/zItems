package fr.traqueur.items.items;

import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.items.ItemProvider;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Item provider for zItems custom items.
 * Allows referencing zItems items from other items via copy-from.
 */
public record ZItemsItemProvider() implements ItemProvider {

    @Override
    public @NotNull Optional<ItemStack> createItem(@Nullable Player player, @NotNull String itemId) {
        ItemsRegistry registry = Registry.get(ItemsRegistry.class);
        if (registry == null) {
            return Optional.empty();
        }

        Item item = registry.getById(itemId);
        if (item == null) {
            return Optional.empty();
        }

        return Optional.of(item.build(player, 1));
    }

    @Override
    public boolean hasItem(@NotNull String itemId) {
        ItemsRegistry registry = Registry.get(ItemsRegistry.class);
        if (registry == null) {
            return false;
        }
        return registry.getById(itemId) != null;
    }
}
