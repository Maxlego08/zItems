package fr.traqueur.items.api.settings;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.items.DurabilityMode;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.settings.models.*;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Configuration settings for a custom item.
 * This record contains all the properties that define how an item appears and behaves.
 * @param baseItem               The base item configuration (material, display name, lore, etc.) - inlined from ItemStackWrapper.
 * @param enchantments           The enchantments applied to the item.
 * @param disabledEnchantments   The enchantments that are disabled on the item.
 * @param attributes             The attributes applied to the item.
 * @param effects                The effects associated with the item.
 * @param rarity                 The rarity level of the item.
 * @param flags                  The item flags that modify item behavior.
 * @param metadata               Additional metadata for the item.
 * @param maxDamage              The maximum durability of the item.
 * @param customModelData        The custom model data value for resource pack integration.
 * @param unbreakable            Whether the item is unbreakable.
 * @param hideTooltip            Whether to hide the tooltip information.
 * @param tooltipStyle           The NamespacedKey of the tooltip style (border/frame) to use, from a resource pack.
 * @param grindstoneEnabled      Whether the item can be repaired on a grindstone.
 * @param maxStackSize           The maximum stack size for the item.
 * @param repairCost             The cost to repair the item.
 * @param damageTypeResistance   The damage type resistances of the item.
 * @param recipe                 The crafting recipe settings for the item.
 * @param attributeMergeStrategy The strategy for merging attributes.
 * @param nbEffectsView          The number of effects to display in the item tooltip.
 * @param baseEffectsVisible     Whether base effects are visible in the tooltip.
 * @param additionalEffectsVisible Whether additional effects are visible in the tooltip.
 * @param allowAdditionalEffects Whether additional effects can be added to the item.
 * @param disabledEffects        The list of effects that are disabled for the item.
 * @param trackable              Whether the item is trackable.
 * @param anvilEnabled           Whether the item can be used in an anvil.
 * @param enchantingTableEnabled Whether the item can be enchanted at an enchanting table.
 */
public record ItemSettings(
        @Options(inline = true)
        ItemStackWrapper baseItem,

        @Options(optional = true)
        List<EnchantmentWrapper> enchantments,

        @Options(optional = true)
        List<DisabledEnchantment> disabledEnchantments,

        @Options(optional = true)
        List<AttributeWrapper> attributes,

        @Options(optional = true)
        List<Effect> effects,

        @Options(optional = true)
        ItemRarity rarity,

        @Options(optional = true)
        List<ItemFlag> flags,

        @Options(optional = true)
        List<ItemMetadata> metadata,

        @Options(optional = true)
        @DefaultInt(-1)
        int maxDamage,

        @Options(optional = true)
        @DefaultInt(-1)
        int customModelData,

        @Options(optional = true)
        @DefaultBool(false)
        boolean unbreakable,

        @Options(optional = true)
        DurabilityMode durabilityMode,

        @Options(optional = true)
        @DefaultBool(false)
        boolean hideTooltip,

        @Options(optional = true)
        NamespacedKey tooltipStyle,

        @Options(optional = true)
        @DefaultBool(false)
        boolean grindstoneEnabled,

        @Options(optional = true)
        @DefaultInt(-1)
        int maxStackSize,

        @Options(optional = true)
        @DefaultInt(-1) int repairCost,

        @Options(optional = true)
        Tag<DamageType> damageTypeResistance,

        @Options(optional = true)
        RecipeWrapper recipe,

        @Options(optional = true)
        @AttributeMergeStrategy.DefaultStrategy(AttributeMergeStrategy.ADD)
        AttributeMergeStrategy attributeMergeStrategy,

        @Options(optional = true)
        @DefaultInt(-1)
        int nbEffectsView,

        @Options(optional = true)
        @DefaultBool(true)
        boolean baseEffectsVisible,

        @Options(optional = true)
        @DefaultBool(true)
        boolean additionalEffectsVisible,

        @Options(optional = true)
        @DefaultBool(true)
        boolean allowAdditionalEffects,

        @Options(optional = true)
        List<String> disabledEffects,

        @Options(optional = true)
        @DefaultBool(true)
        boolean trackable,

        @Options(optional = true)
        @DefaultBool(true)
        boolean anvilEnabled,

        @Options(optional = true)
        @DefaultBool(true)
        boolean enchantingTableEnabled
) implements Loadable {}