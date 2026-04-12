package fr.traqueur.items.api.settings.models;

import fr.traqueur.items.api.placeholders.PlaceholderParser;
import fr.traqueur.items.api.registries.ItemProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lightweight configuration for creating ItemStacks.
 * This is different from {@code ItemSettings} which is for complete custom items.
 *
 * <p>This allows two modes:
 * <ul>
 *   <li>Simple mode: Create a basic ItemStack with material, amount, and optional display properties</li>
 *   <li>Copy-from mode: Copy from a plugin's item (zItems, ItemsAdder, Nexo, Oraxen, etc.) with optional overrides</li>
 * </ul>
 *
 * <p>Example YAML (simple mode):
 * <pre>
 * material: DIAMOND
 * amount: 64
 * display-name: "&lt;red&gt;Special Diamond"
 * </pre>
 *
 * <p>Example YAML (copy-from mode - basic):
 * <pre>
 * copy-from:
 *   plugin-name: "itemsadder"
 *   item-id: "my_custom_sword"
 * amount: 1
 * </pre>
 *
 * <p>Example YAML (copy-from mode - with overrides):
 * <pre>
 * copy-from:
 *   plugin-name: "nexo"
 *   item-id: "ruby_pickaxe"
 * amount: 1
 * display-name: "&lt;gold&gt;Super Ruby Pickaxe"
 * lore:
 *   - "&lt;gray&gt;A legendary tool"
 * </pre>
 *
 * <p>Example YAML (copy-from zItems):
 * <pre>
 * copy-from:
 *   plugin-name: "zitems"
 *   item-id: "my_custom_item"
 * </pre>
 *
 * @param material The material type (optional if copyFrom is provided)
 * @param amount The quantity of the item (default is 1)
 * @param copyFrom Configuration to copy from a plugin's item (optional if material is provided)
 * @param displayName The custom display name (optional, overrides copied item)
 * @param itemName The internal item name (optional, overrides copied item)
 * @param lore The custom lore lines (optional, overrides copied item)
 */
public record ItemStackWrapper(
        @Options(optional = true) Material material,

        @Options(optional = true) @DefaultInt(1) int amount,

        @Options(optional = true) CopyFrom copyFrom,

        @Options(optional = true) String displayName,

        @Options(optional = true) String itemName,

        @Options(optional = true) List<String> lore
) implements Loadable {

    /**
     * Validates the configuration after loading.
     *
     * @throws IllegalArgumentException if amount is less than 1 or if neither material nor copyFrom is specified
     */
    public ItemStackWrapper {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be at least 1");
        }
        if (material == null && copyFrom == null) {
            throw new IllegalArgumentException("Either 'material' or 'copy-from' must be specified");
        }
    }

    /**
     * Builds an ItemStack from these settings.
     *
     * @param player the player for context (used for placeholders)
     * @return the created ItemStack
     * @throws IllegalStateException if the item source cannot be resolved
     */
    public @NotNull ItemStack build(@Nullable Player player) {
        // Get base ItemStack from either copyFrom or material
        ItemStack result = getBaseItemStack(player);

        // Set amount
        result.setAmount(amount);

        // Apply all overrides
        applyOverrides(result, player);

        return result;
    }

    /**
     * Gets the base ItemStack from either copyFrom provider or material.
     *
     * @param player the player for context
     * @return the base ItemStack
     * @throws IllegalStateException if the item source cannot be resolved
     */
    private @NotNull ItemStack getBaseItemStack(@Nullable Player player) {
        if (copyFrom != null) {
            ItemProviderRegistry providerRegistry = Registry.get(ItemProviderRegistry.class);
            if (providerRegistry == null) {
                throw new IllegalStateException("ItemProviderRegistry is not registered");
            }

            Optional<ItemStack> item = providerRegistry.createItem(
                    copyFrom.pluginName(),
                    player,
                    copyFrom.itemId()
            );

            if (item.isEmpty()) {
                throw new IllegalStateException("Could not create item from provider '" + copyFrom.pluginName()
                        + "' with ID '" + copyFrom.itemId() + "'");
            }

            return item.get().clone();
        }

        return new ItemStack(material);
    }

    /**
     * Applies display name, item name, and lore overrides to the ItemStack.
     *
     * @param item the ItemStack to modify
     * @param player the player for placeholder parsing
     */
    private void applyOverrides(@NotNull ItemStack item, @Nullable Player player) {
        // Override display name
        if (displayName != null && !displayName.isEmpty()) {
            String parsed = PlaceholderParser.parsePlaceholders(player, displayName);
            Component displayNameComponent = MessageUtil.parseMessage(parsed);
            ItemUtil.setDisplayName(item, displayNameComponent);
        }

        // Override item name
        if (itemName != null && !itemName.isEmpty()) {
            String parsed = PlaceholderParser.parsePlaceholders(player, itemName);
            Component itemNameComponent = MessageUtil.parseMessage(parsed);
            ItemUtil.setItemName(item, itemNameComponent);
        }

        // Override lore
        if (lore != null && !lore.isEmpty()) {
            List<Component> loreComponents = new ArrayList<>();
            for (String loreLine : lore) {
                String parsed = PlaceholderParser.parsePlaceholders(player, loreLine);
                Component loreComponent = MessageUtil.parseMessage(parsed);
                loreComponents.add(loreComponent);
            }
            ItemUtil.setLore(item, loreComponents);
        }
    }
}
