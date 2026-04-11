package fr.traqueur.items.items;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.PlatformType;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.items.DurabilityMode;
import fr.traqueur.items.api.events.ItemBuildEvent;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.placeholders.PlaceholderParser;
import fr.traqueur.items.api.settings.ItemSettings;
import fr.traqueur.items.api.settings.models.EnchantmentWrapper;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.items.utils.AttributeUtil;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ZItem(String id, @Options(inline = true) ItemSettings settings) implements Item, Loadable {

    private static final ItemsPlugin PLUGIN = JavaPlugin.getPlugin(ItemsPlugin.class);

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
        List<Component> effectLore = generateEffectLore(player);
        List<Component> combinedLore = buildCombinedLore(player, effectLore);

        ItemStack itemStack = settings.baseItem().build(player);
        ItemUtil.setLore(itemStack, combinedLore);

        boolean hideAttrTooltip = settings.flags() != null && settings.flags().contains(ItemFlag.HIDE_ATTRIBUTES);
        AttributeUtil.applyAttributes(itemStack, settings.attributes(), PLUGIN, settings.attributeMergeStrategy());

        if (PlatformType.isPaper() && hideAttrTooltip) {
            applyPaperAttributeTooltip(itemStack);
        }

        return itemStack;
    }

    private List<Component> generateEffectLore(@Nullable Player player) {
        if (settings.effects() == null || settings.effects().isEmpty()) {
            return List.of();
        }
        EffectsManager effectsManager = PLUGIN.getManager(EffectsManager.class);
        return effectsManager.generateBaseEffectLore(player, settings.effects(), settings);
    }

    private List<Component> buildCombinedLore(@Nullable Player player, List<Component> effectLore) {
        List<Component> combined = new ArrayList<>();
        if (settings.baseItem().lore() != null) {
            settings.baseItem().lore().stream()
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

        meta.setHideTooltip(settings.hideTooltip());

        if (settings.tooltipStyle() != null) {
            meta.setTooltipStyle(settings.tooltipStyle());
        }
        if (settings.maxStackSize() > 0) {
            meta.setMaxStackSize(settings.maxStackSize());
        }
        if (settings.rarity() != null) {
            meta.setRarity(settings.rarity());
        }

        applyItemFlags(meta);
        applyRepairCost(meta);
        applyDamageResistance(meta);

        itemStack.setItemMeta(meta);
    }

    private void applyEnchantments(ItemMeta meta) {
        if (settings.enchantments() == null) {
            return;
        }
        for (EnchantmentWrapper enchantment : settings.enchantments()) {
            meta.addEnchant(enchantment.enchantment(), enchantment.level(), true);
        }
    }

    private void applyDurability(ItemMeta meta) {
        DurabilityMode mode = settings.durabilityMode() != null ? settings.durabilityMode() : DurabilityMode.VANILLA;

        if (mode == DurabilityMode.CUSTOM) {
            meta.setUnbreakable(true);
            int maxDur = settings.maxDamage() > 0 ? settings.maxDamage() : 100;
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            Keys.CUSTOM_DURABILITY.set(pdc, maxDur);
            Keys.CUSTOM_MAX_DURABILITY.set(pdc, maxDur);
        } else {
            if (meta instanceof Damageable damageable && settings.maxDamage() > 0) {
                damageable.setMaxDamage(settings.maxDamage());
                damageable.setDamage(0);
            }
            meta.setUnbreakable(settings.unbreakable());
        }
        Keys.DURABILITY_MODE.set(meta.getPersistentDataContainer(), mode.name());
    }

    private void applyCustomModelData(ItemMeta meta) {
        if (settings.customModelData() <= 0) {
            return;
        }
        if (PlatformType.isPaper()) {
            meta.getCustomModelDataComponent().setFloats(List.of((float) settings.customModelData()));
        } else {
            meta.setCustomModelData(settings.customModelData());
        }
    }

    private void applyItemFlags(ItemMeta meta) {
        if (settings.flags() == null) {
            return;
        }
        // On Paper, HIDE_ATTRIBUTES is handled via the Data Component API (showInTooltip),
        // so we filter it out here to avoid conflicts with ItemMeta's deprecated flag path.
        ItemFlag[] flags = PlatformType.isPaper()
                ? settings.flags().stream().filter(f -> f != ItemFlag.HIDE_ATTRIBUTES).toArray(ItemFlag[]::new)
                : settings.flags().toArray(ItemFlag[]::new);
        if (flags.length > 0) {
            meta.addItemFlags(flags);
        }
    }

    private void applyRepairCost(ItemMeta meta) {
        if (meta instanceof Repairable repairable && settings.repairCost() >= 0) {
            repairable.setRepairCost(settings.repairCost());
        }
    }

    private void applyDamageResistance(ItemMeta meta) {
        if (settings.damageTypeResistance() != null) {
            meta.setDamageResistant(settings.damageTypeResistance());
        }
    }

    private void applyPaperAttributeTooltip(ItemStack itemStack) {
        // On Paper 1.21.5+, hide the attribute section via TOOLTIP_DISPLAY data component.
        // ItemFlag.HIDE_ATTRIBUTES on ItemMeta is deprecated and does not work when ATTRIBUTE_MODIFIERS
        // was written directly via setData(), so we use the data component API instead.
        io.papermc.paper.datacomponent.item.TooltipDisplay.Builder tdBuilder =
                io.papermc.paper.datacomponent.item.TooltipDisplay.tooltipDisplay();
        io.papermc.paper.datacomponent.item.TooltipDisplay existing =
                itemStack.getData(io.papermc.paper.datacomponent.DataComponentTypes.TOOLTIP_DISPLAY);
        if (existing != null) {
            tdBuilder.hideTooltip(existing.hideTooltip());
            if (!existing.hiddenComponents().isEmpty()) {
                tdBuilder.hiddenComponents(existing.hiddenComponents());
            }
        }
        tdBuilder.addHiddenComponents(io.papermc.paper.datacomponent.DataComponentTypes.ATTRIBUTE_MODIFIERS);
        itemStack.setData(io.papermc.paper.datacomponent.DataComponentTypes.TOOLTIP_DISPLAY, tdBuilder.build());
    }

    private void applyEffects(@Nullable Player player, ItemStack itemStack) {
        if (settings.effects() == null || settings.effects().isEmpty()) {
            return;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            Keys.EFFECTS.set(meta.getPersistentDataContainer(), new ArrayList<>(settings.effects()));
            itemStack.setItemMeta(meta);
        }
        for (Effect effect : settings.effects()) {
            PLUGIN.getDispatcher().applyNoEventEffect(player, itemStack, effect);
        }
    }

    private void applyMetadata(@Nullable Player player, ItemStack itemStack) {
        if (settings.metadata() == null || settings.metadata().isEmpty()) {
            return;
        }
        for (var metadata : settings.metadata()) {
            metadata.apply(itemStack, player);
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