package fr.traqueur.items.api.items;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.settings.models.AttributeWrapper;
import fr.traqueur.items.api.settings.models.CopyFrom;
import fr.traqueur.items.api.settings.models.DisabledEnchantment;
import fr.traqueur.items.api.settings.models.EnchantmentWrapper;
import fr.traqueur.items.api.settings.models.RecipeWrapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a custom item in the plugin.
 *
 * <p>All item properties are directly accessible as methods on this interface,
 * so external developers do not need to know about internal configuration classes.</p>
 */
public interface Item {

    /**
     * Gets the unique identifier for this item.
     */
    @NotNull
    String id();

    // ── Base item (from ItemStackWrapper) ─────────────────────────────────

    /** The Bukkit material type, or {@code null} when using copy-from. */
    @Nullable Material material();

    /** The stack size (default 1). */
    int amount();

    /** Copy-from configuration, or {@code null} when using a direct material. */
    @Nullable CopyFrom copyFrom();

    /** Legacy/MiniMessage display name string, or {@code null} if unset. */
    @Nullable String displayName();

    /** Legacy/MiniMessage item name string, or {@code null} if unset. */
    @Nullable String itemName();

    /** Raw lore lines (MiniMessage/legacy), or {@code null} if unset. */
    @Nullable List<String> lore();

    // ── Item settings ─────────────────────────────────────────────────────

    /** Enchantments to apply on build, or {@code null} if none. */
    @Nullable List<EnchantmentWrapper> enchantments();

    /** Enchantments that cannot be added to this item, or {@code null} if none. */
    @Nullable List<DisabledEnchantment> disabledEnchantments();

    /** Attribute modifiers to apply on build, or {@code null} if none. */
    @Nullable List<AttributeWrapper> attributes();

    /** Base effects defined in the item's config, or {@code null} if none. */
    @Nullable List<Effect> effects();

    /** Item rarity, or {@code null} to use the default. */
    @Nullable ItemRarity rarity();

    /** Item flags, or {@code null} if none. */
    @Nullable List<ItemFlag> flags();

    /** Additional metadata entries, or {@code null} if none. */
    @Nullable List<ItemMetadata> metadata();

    /** Maximum damage/durability ({@code -1} = vanilla default). */
    int maxDamage();

    /** Custom model data value ({@code -1} = none). */
    int customModelData();

    /** Whether the item is unbreakable. */
    boolean unbreakable();

    /** Durability mode, or {@code null} to use vanilla. */
    @Nullable DurabilityMode durabilityMode();

    /** Whether the item tooltip is hidden. */
    boolean hideTooltip();

    /** Tooltip style key from a resource pack, or {@code null} if none. */
    @Nullable NamespacedKey tooltipStyle();

    /** Whether the item can be repaired on a grindstone. */
    boolean grindstoneEnabled();

    /** Maximum stack size ({@code -1} = default). */
    int maxStackSize();

    /** Anvil repair cost ({@code -1} = default). */
    int repairCost();

    /** Damage type resistance tag, or {@code null} if none. */
    @Nullable Tag<DamageType> damageTypeResistance();

    /** Crafting recipe configuration, or {@code null} if none. */
    @Nullable RecipeWrapper recipe();

    /** Strategy for merging attribute modifiers. */
    @NotNull AttributeMergeStrategy attributeMergeStrategy();

    /** Number of effects shown in lore ({@code -1} = use global default, {@code 0} = none). */
    int nbEffectsView();

    /** Whether base effects are shown in lore. */
    boolean baseEffectsVisible();

    /** Whether additional (applied) effects are shown in lore. */
    boolean additionalEffectsVisible();

    /** Whether additional effects may be applied to this item via commands/GUI. */
    boolean allowAdditionalEffects();

    /** Effect IDs that cannot be applied to this item, or {@code null} if none. */
    @Nullable List<String> disabledEffects();

    /** Whether placing this item as a block is tracked for correct drops. */
    boolean trackable();

    /** Whether this item can be used in an anvil. */
    boolean anvilEnabled();

    /** Whether this item can be enchanted at an enchanting table. */
    boolean enchantingTableEnabled();

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Returns a human-readable name: display name, item name, or {@link #id()} as fallback.
     */
    default String representativeName() {
        if (displayName() == null && itemName() == null) {
            return id();
        }
        return displayName() != null ? displayName() : itemName();
    }

    /**
     * Builds an ItemStack for this item.
     *
     * @param player the player context for placeholder resolution (can be null)
     * @param amount the stack size
     * @return the built ItemStack
     */
    @NotNull
    ItemStack build(@Nullable Player player, int amount);
}
