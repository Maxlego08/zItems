package fr.traqueur.items.items;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.MinecraftVersion;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.events.ItemBuildEvent;
import fr.traqueur.items.api.items.DurabilityMode;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.items.ItemMetadata;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.placeholders.PlaceholderParser;
import fr.traqueur.items.api.registries.ItemProviderRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.services.AttributeTooltipService;
import fr.traqueur.items.api.settings.models.AttributeMergeStrategy;
import fr.traqueur.items.api.settings.models.AttributeWrapper;
import fr.traqueur.items.api.settings.models.CopyFrom;
import fr.traqueur.items.api.settings.models.DisabledEnchantment;
import fr.traqueur.items.api.settings.models.EnchantmentWrapper;
import fr.traqueur.items.api.settings.models.RecipeWrapper;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.items.utils.AttributeUtil;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

public record ZItem(
        String id,

        // ── Base item (ItemStackWrapper fields, inlined) ───────────────────────
        @Options(optional = true) Material material,
        @Options(optional = true) @DefaultInt(1) int amount,
        @Options(optional = true) CopyFrom copyFrom,
        @Options(optional = true) String displayName,
        @Options(optional = true) String itemName,
        @Options(optional = true) List<String> lore,

        // ── Item settings ──────────────────────────────────────────────────────
        @Options(optional = true) List<EnchantmentWrapper> enchantments,
        @Options(optional = true) List<DisabledEnchantment> disabledEnchantments,
        @Options(optional = true) List<AttributeWrapper> attributes,
        @Options(optional = true) List<Effect> effects,
        @Options(optional = true) ItemRarity rarity,
        @Options(optional = true) List<ItemFlag> flags,
        @Options(optional = true) List<ItemMetadata> metadata,
        @Options(optional = true) @DefaultInt(-1) int maxDamage,
        @Options(optional = true) @DefaultInt(-1) int customModelData,
        @Options(optional = true) @DefaultBool(false) boolean unbreakable,
        @Options(optional = true) DurabilityMode durabilityMode,
        @Options(optional = true) @DefaultBool(false) boolean hideTooltip,
        @Options(optional = true) NamespacedKey tooltipStyle,
        @Options(optional = true) @DefaultBool(false) boolean grindstoneEnabled,
        @Options(optional = true) @DefaultInt(-1) int maxStackSize,
        @Options(optional = true) @DefaultInt(-1) int repairCost,
        @Options(optional = true) Tag<DamageType> damageTypeResistance,
        @Options(optional = true) RecipeWrapper recipe,
        @Options(optional = true) @AttributeMergeStrategy.DefaultStrategy(AttributeMergeStrategy.ADD) AttributeMergeStrategy attributeMergeStrategy,
        @Options(optional = true) @DefaultInt(-1) int nbEffectsView,
        @Options(optional = true) @DefaultBool(true) boolean baseEffectsVisible,
        @Options(optional = true) @DefaultBool(true) boolean additionalEffectsVisible,
        @Options(optional = true) @DefaultBool(true) boolean allowAdditionalEffects,
        @Options(optional = true) List<String> disabledEffects,
        @Options(optional = true) @DefaultBool(true) boolean trackable,
        @Options(optional = true) @DefaultBool(true) boolean anvilEnabled,
        @Options(optional = true) @DefaultBool(true) boolean enchantingTableEnabled
) implements Item, Loadable {

    // Record component accessors satisfy the Item interface directly — no @Override needed.

    private static final ItemsPlugin PLUGIN = JavaPlugin.getPlugin(ItemsPlugin.class);

    /**
     * Optional service for hiding attribute tooltips (Paper 1.21.5+ only).
     */
    private static final AttributeTooltipService ATTRIBUTE_TOOLTIP_SERVICE;
    static {
        Iterator<AttributeTooltipService> it = ServiceLoader.load(AttributeTooltipService.class).iterator();
        AttributeTooltipService found = it.hasNext() ? it.next() : null;
        boolean paper1215plus = PlatformType.isPaper()
                && MinecraftVersion.current().isAtLeast(MinecraftVersion.parse("1.21.5"));
        ATTRIBUTE_TOOLTIP_SERVICE = paper1215plus ? found : null;
    }

    // ── Build ──────────────────────────────────────────────────────────────────

    @Override
    public @NotNull ItemStack build(@Nullable Player player, int amount) {
        ItemStack itemStack = buildBaseItem(player);
        applyItemMeta(itemStack);
        applyEffects(player, itemStack);
        applyMetadata(player, itemStack);
        applyItemId(itemStack);

        ItemBuildEvent event = new ItemBuildEvent(player, this, itemStack);
        PLUGIN.getServer().getPluginManager().callEvent(event);
        return event.getItemStack();
    }

    private ItemStack buildBaseItem(@Nullable Player player) {
        // Resolve the base ItemStack from material or copy-from
        ItemStack itemStack;
        if (copyFrom != null) {
            ItemProviderRegistry providerRegistry = Registry.get(ItemProviderRegistry.class);
            if (providerRegistry == null) {
                throw new IllegalStateException("ItemProviderRegistry is not registered");
            }
            Optional<ItemStack> provided = providerRegistry.createItem(copyFrom.pluginName(), player, copyFrom.itemId());
            if (provided.isEmpty()) {
                throw new IllegalStateException("Could not create item from provider '" + copyFrom.pluginName()
                        + "' with ID '" + copyFrom.itemId() + "'");
            }
            itemStack = provided.get().clone();
        } else {
            itemStack = new ItemStack(material);
        }
        itemStack.setAmount(this.amount);

        // Apply display name and item name overrides
        if (displayName != null && !displayName.isEmpty()) {
            ItemUtil.setDisplayName(itemStack,
                    MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, displayName)));
        }
        if (itemName != null && !itemName.isEmpty()) {
            ItemUtil.setItemName(itemStack,
                    MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, itemName)));
        }

        // Build lore: base lore + effect lore
        List<Component> effectLore = generateEffectLore(player);
        List<Component> combinedLore = buildCombinedLore(player, effectLore);
        ItemUtil.setLore(itemStack, combinedLore);

        // Apply attributes
        boolean hideAttrTooltip = flags != null && flags.contains(ItemFlag.HIDE_ATTRIBUTES);
        AttributeUtil.applyAttributes(itemStack, attributes, PLUGIN, attributeMergeStrategy);
        if (hideAttrTooltip && ATTRIBUTE_TOOLTIP_SERVICE != null) {
            ATTRIBUTE_TOOLTIP_SERVICE.hideAttributesTooltip(itemStack);
        }

        return itemStack;
    }

    private List<Component> generateEffectLore(@Nullable Player player) {
        if (effects == null || effects.isEmpty()) {
            return List.of();
        }
        EffectsManager effectsManager = PLUGIN.getManager(EffectsManager.class);
        return effectsManager.generateBaseEffectLore(player, effects, this);
    }

    private List<Component> buildCombinedLore(@Nullable Player player, List<Component> effectLore) {
        List<Component> combined = new ArrayList<>();
        if (lore != null) {
            lore.stream()
                    .map(str -> MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, str)))
                    .forEach(combined::add);
        }
        combined.addAll(effectLore);
        return combined;
    }

    private void applyItemMeta(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return;
        }

        applyEnchantments(meta);
        applyDurability(meta);
        applyCustomModelData(meta);

        meta.setHideTooltip(hideTooltip);

        if (tooltipStyle != null) {
            meta.setTooltipStyle(tooltipStyle);
        }
        if (maxStackSize > 0) {
            meta.setMaxStackSize(maxStackSize);
        }
        if (rarity != null) {
            meta.setRarity(rarity);
        }

        applyItemFlags(meta);
        applyRepairCost(meta);
        applyDamageResistance(meta);

        itemStack.setItemMeta(meta);
    }

    private void applyEnchantments(ItemMeta meta) {
        if (enchantments == null) {
            return;
        }
        for (EnchantmentWrapper enchantment : enchantments) {
            meta.addEnchant(enchantment.enchantment(), enchantment.level(), true);
        }
    }

    private void applyDurability(ItemMeta meta) {
        DurabilityMode mode = durabilityMode != null ? durabilityMode : DurabilityMode.VANILLA;

        if (mode == DurabilityMode.CUSTOM) {
            meta.setUnbreakable(true);
            int maxDur = maxDamage > 0 ? maxDamage : 100;
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            Keys.CUSTOM_DURABILITY.set(pdc, maxDur);
            Keys.CUSTOM_MAX_DURABILITY.set(pdc, maxDur);
        } else {
            if (meta instanceof Damageable damageable && maxDamage > 0) {
                damageable.setMaxDamage(maxDamage);
                damageable.setDamage(0);
            }
            meta.setUnbreakable(unbreakable);
        }
        Keys.DURABILITY_MODE.set(meta.getPersistentDataContainer(), mode.name());
    }

    private void applyCustomModelData(ItemMeta meta) {
        if (customModelData <= 0) {
            return;
        }
        if (PlatformType.isPaper()) {
            meta.getCustomModelDataComponent().setFloats(List.of((float) customModelData));
        } else {
            meta.setCustomModelData(customModelData);
        }
    }

    private void applyItemFlags(ItemMeta meta) {
        if (flags == null) {
            return;
        }
        // On Paper 1.21.5+, HIDE_ATTRIBUTES is handled via TOOLTIP_DISPLAY data component.
        ItemFlag[] itemFlags = ATTRIBUTE_TOOLTIP_SERVICE != null
                ? flags.stream().filter(f -> f != ItemFlag.HIDE_ATTRIBUTES).toArray(ItemFlag[]::new)
                : flags.toArray(ItemFlag[]::new);
        if (itemFlags.length > 0) {
            meta.addItemFlags(itemFlags);
        }
    }

    private void applyRepairCost(ItemMeta meta) {
        if (meta instanceof Repairable repairable && repairCost >= 0) {
            repairable.setRepairCost(repairCost);
        }
    }

    private void applyDamageResistance(ItemMeta meta) {
        if (damageTypeResistance != null) {
            meta.setDamageResistant(damageTypeResistance);
        }
    }

    private void applyEffects(@Nullable Player player, ItemStack itemStack) {
        if (effects == null || effects.isEmpty()) {
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            Keys.EFFECTS.set(meta.getPersistentDataContainer(), new ArrayList<>(effects));
            itemStack.setItemMeta(meta);
        }
        for (Effect effect : effects) {
            PLUGIN.getDispatcher().applyNoEventEffect(player, itemStack, effect);
        }
    }

    private void applyMetadata(@Nullable Player player, ItemStack itemStack) {
        if (metadata == null || metadata.isEmpty()) {
            return;
        }
        for (ItemMetadata m : metadata) {
            m.apply(itemStack, player);
        }
    }

    private void applyItemId(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            Keys.ITEM_ID.set(meta.getPersistentDataContainer(), id);
            itemStack.setItemMeta(meta);
        }
    }
}
