package fr.traqueur.items.effects;

import fr.traqueur.items.Messages;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectApplicationResult;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.effects.EffectRepresentation;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.managers.ItemsManager;
import fr.traqueur.items.api.placeholders.PlaceholderParser;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.settings.ItemSettings;
import fr.traqueur.items.api.settings.Settings;
import fr.traqueur.items.api.utils.ItemUtil;
import fr.traqueur.items.api.utils.MessageUtil;
import fr.traqueur.items.serialization.Keys;
import fr.traqueur.items.settings.PluginSettings;
import fr.traqueur.recipes.api.RecipeType;
import fr.traqueur.recipes.impl.domains.ItemRecipe;
import fr.traqueur.recipes.impl.domains.ingredients.StrictItemStackIngredient;
import fr.traqueur.recipes.impl.domains.recipes.RecipeBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;
import java.util.stream.Collectors;

public class ZEffectsManager implements EffectsManager {

    private static final PlainTextComponentSerializer PLAIN_TEXT_SERIALIZER = PlainTextComponentSerializer.plainText();

    private final List<ItemRecipe> recipes;

    public ZEffectsManager() {
        this.recipes = new ArrayList<>();
    }

    @Override
    public void loadRecipes() {
        for (ItemRecipe recipe : recipes) {
            this.getPlugin().getRecipesManager().removeRecipe(recipe);
        }
        for (Effect effect : Registry.get(EffectsRegistry.class).getAll()) {
            if (effect.representation() != null) {
                if (effect.representation().applicatorType() == EffectRepresentation.ApplicatorType.SMITHING_TABLE) {
                    createSmithingCraft(effect);
                }
            }
        }
    }

    private void createSmithingCraft(Effect effect) {
        EffectRepresentation representation = effect.representation();
        Set<Material> materials = new HashSet<>();
        List<Material> effectsMat = effect.settings().applicableMaterials();
        if(effectsMat != null && !effectsMat.isEmpty()) {
            materials.addAll(effectsMat);
        }
        List<Tag<Material>> effectsTags = effect.settings().applicableTags();
        if(effectsTags != null && !effectsTags.isEmpty()) {
            effectsTags.forEach(tag -> materials.addAll(tag.getValues()));
        }

        materials.forEach(material -> {
            ItemStack result = new ItemStack(material);
            this.applyEffect(null, result, effect);
            ItemRecipe recipe = new RecipeBuilder().setType(RecipeType.SMITHING_TRANSFORM)
                    .addIngredient(representation.getTemplateIngredient())
                    .addIngredient(material)
                    .addIngredient(new StrictItemStackIngredient(representation.item().build(null)))
                    .setResult(result)
                    .setAmount(1)
                    .setName("effect" + effect.id() + "_" + material.name().toLowerCase()).build();
            this.recipes.add(recipe);
            this.getPlugin().getRecipesManager().addRecipe(recipe);
            Logger.debug("Registered smithing recipe for effect {} with material {}", effect.id(), material.name());
        });
    }

    @Override
    public EffectApplicationResult applyEffect(Player player, ItemStack item, Effect effect) {
        return applyEffect(player, item, effect, true);
    }

    /**
     * Internal method to apply an effect with control over lore updating.
     *
     * @param player the player applying the effect
     * @param item the item to apply the effect to
     * @param effect the effect to apply
     * @param updateLore whether to update the item's lore after applying
     * @return the result of the effect application
     */
    private EffectApplicationResult applyEffect(Player player, ItemStack item, Effect effect, boolean updateLore) {
        // Get all existing effects from the item
        List<Effect> existingEffects = Keys.EFFECTS.get(
                item.getItemMeta().getPersistentDataContainer(),
                new ArrayList<>()
        );

        // Check if effect is already present
        if (existingEffects.stream().anyMatch(e -> e.id().equals(effect.id()))) {
            Logger.debug("Effect {} is already present on the item", effect.id());
            return EffectApplicationResult.ALREADY_PRESENT;
        }

        // Check if this is a custom item and validate restrictions
        ItemsManager itemsManager = this.getPlugin().getManager(ItemsManager.class);
        if (itemsManager != null) {
            Optional<Item> customItem = itemsManager.getCustomItem(item);
            if (customItem.isPresent()) {
                Item customItemInstance = customItem.get();

                // Check if additional effects are allowed
                if (!customItemInstance.settings().allowAdditionalEffects()) {
                    Logger.debug("Cannot apply effect {} to item {}: additional effects are not allowed",
                            effect.type(), customItemInstance.id());
                    return EffectApplicationResult.NOT_ALLOWED;
                }

                // Check if this specific effect is disabled for this item
                if (customItemInstance.settings().disabledEffects() != null &&
                        customItemInstance.settings().disabledEffects().contains(effect.id())) {
                    Logger.debug("Cannot apply effect {} to item {}: this effect is disabled for this item",
                            effect.id(), customItemInstance.id());
                    return EffectApplicationResult.DISABLED;
                }
            }
        }

        // Validate incompatibilities before applying
        EffectApplicationResult compatibilityResult = validateCompatibility(item, effect);
        if (compatibilityResult != EffectApplicationResult.SUCCESS) {
            Logger.debug("Cannot apply effect {} to item: {}", effect.type(), compatibilityResult);
            return compatibilityResult;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            List<Effect> effects = new ArrayList<>(Keys.EFFECTS.get(container, new ArrayList<>()));
            effects.add(effect);
            Keys.EFFECTS.set(container, effects);
            item.setItemMeta(meta);
        }

        this.getPlugin().getDispatcher().applyNoEventEffect(player, item, effect);

        // Update item lore to show the new effect (only if requested)
        if (updateLore) {
            List<Effect> allEffects = Keys.EFFECTS.get(
                    item.getItemMeta().getPersistentDataContainer(),
                    new ArrayList<>()
            );
            updateItemLoreWithEffects(player, item, allEffects);
        }

        return EffectApplicationResult.SUCCESS;
    }

    /**
     * Validates that the new effect is compatible with all existing effects on the item.
     * Checks bidirectional incompatibilities.
     *
     * @param item the item to check
     * @param newEffect the effect to be applied
     * @return EffectApplicationResult indicating compatibility status
     */
    private EffectApplicationResult validateCompatibility(ItemStack item, Effect newEffect) {
        // Get the handler for the new effect
        HandlersRegistry registry = Registry.get(HandlersRegistry.class);
        EffectHandler<?> newHandler = registry.getById(newEffect.type());
        if (newHandler == null) {
            Logger.warning("Handler not found for effect type: {}", newEffect.type());
            return EffectApplicationResult.HANDLER_NOT_FOUND;
        }

        // Get incompatible handlers for the new effect
        Set<Class<? extends EffectHandler<?>>> newIncompatibles = newHandler.getIncompatibleHandlers();

        // Get all existing effects from the item
        List<Effect> existingEffects = Keys.EFFECTS.get(item.getItemMeta().getPersistentDataContainer(), new ArrayList<>());

        if (existingEffects == null || existingEffects.isEmpty()) {
            return EffectApplicationResult.SUCCESS; // No existing effects, nothing to conflict with
        }

        // Check each existing effect for incompatibility
        for (Effect existingEffect : existingEffects) {
            EffectHandler<?> existingHandler = registry.getById(existingEffect.type());
            if (existingHandler == null) {
                continue;
            }

            // Check if new effect is incompatible with existing effect
            if (newIncompatibles.contains(existingHandler.getClass())) {
                Logger.debug("Effect {} is incompatible with existing effect {}",
                        newEffect.type(), existingEffect.type());
                return EffectApplicationResult.INCOMPATIBLE;
            }

            // Check bidirectional: if existing effect is incompatible with new effect
            Set<Class<? extends EffectHandler<?>>> existingIncompatibles = existingHandler.getIncompatibleHandlers();
            if (existingIncompatibles.contains(newHandler.getClass())) {
                Logger.debug("Existing effect {} is incompatible with new effect {}",
                        existingEffect.type(), newEffect.type());
                return EffectApplicationResult.INCOMPATIBLE;
            }
        }

        return EffectApplicationResult.SUCCESS;
    }

    @Override
    public boolean canApplyEffectTo(Effect effect, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        // Use the effect settings to check applicability
        return effect.settings().canApplyTo(item);
    }

    @Override
    public boolean hasEffects(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        List<Effect> effects = Keys.EFFECTS.get(
            item.getItemMeta().getPersistentDataContainer(),
            new ArrayList<>()
        );

        return effects != null && !effects.isEmpty();
    }

    @Override
    public void updateItemLoreWithEffects(Player player, ItemStack item, List<Effect> effects) {
        if (item == null || !item.hasItemMeta() || effects == null) {
            return;
        }

        ItemsManager itemsManager = this.getPlugin().getManager(ItemsManager.class);
        if (itemsManager != null) {
            Optional<Item> customItem = itemsManager.getCustomItem(item);
            if (customItem.isPresent()) {
                updateItemLoreForCustomItem(player, item, customItem.get(), effects);
            } else {
                updateVanillaItemLoreWithEffects(player, item, effects);
            }
        }
    }

    @Override
    public void reapplyNoEventEffects(Player player, ItemStack item, List<Effect> effects) {
        if (item == null || effects == null || effects.isEmpty()) {
            return;
        }

        for (Effect effect : effects) {
            this.getPlugin().getDispatcher().applyNoEventEffect(player, item, effect);
        }
    }

    /**
     * Updates the item's lore for a custom item with the given effects.
     *
     * @param item the item to update
     * @param customItem the custom item definition
     * @param allEffects all effects to display
     */
    private void updateItemLoreForCustomItem(Player player, ItemStack item, Item customItem, List<Effect> allEffects) {
        // Separate base effects and additional effects
        List<Effect> baseEffects = customItem.settings().effects() != null
                ? customItem.settings().effects()
                : List.of();

        List<String> baseEffectIds = baseEffects.stream()
                .map(Effect::id)
                .toList();

        List<Effect> additionalEffects = allEffects.stream()
                .filter(effect -> !baseEffectIds.contains(effect.id()))
                .collect(Collectors.toList());

        // Generate effect lore
        List<Component> effectLoreLines = generateEffectLore(
                player,
                baseEffects,
                additionalEffects,
                customItem.settings()
        );

        // Combine base lore with effect lore
        List<Component> combinedLore = new ArrayList<>();
        if (customItem.settings().baseItem().lore() != null) {
            combinedLore.addAll(customItem.settings().baseItem().lore().stream().map(str -> MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, str))).toList());
        }
        combinedLore.addAll(effectLoreLines);

        // Update item lore
        ItemUtil.setLore(item, combinedLore);

        Logger.debug("Updated item lore for {} with {} total effects",
                customItem.id(), allEffects.size());
    }

    /**
     * Updates the lore of a vanilla item with the given effects.
     *
     * @param item the vanilla item to update
     * @param allEffects all effects to display
     */
    private void updateVanillaItemLoreWithEffects(Player player, ItemStack item, List<Effect> allEffects) {
        if (allEffects.isEmpty()) {
            return; // No effects to display
        }

        // Prepare the reference plain texts
        String titlePlain = PLAIN_TEXT_SERIALIZER.serialize(MessageUtil.MINI_MESSAGE.deserialize(Messages.EFFECTS_LORE_TITLE.get())).trim();

        // Get existing lore (if any)
        List<Component> existingLore = ItemUtil.getLore(item);
        if (existingLore == null) {
            existingLore = new ArrayList<>();
        } else {
            // Find where the effects section starts
            int titleIndex = -1;
            for (int i = 0; i < existingLore.size(); i++) {
                Component line = existingLore.get(i);
                String linePlain = PLAIN_TEXT_SERIALIZER.serialize(line).trim();
                if (!linePlain.isEmpty() && linePlain.equals(titlePlain)) {
                    titleIndex = i;
                    break;
                }
            }

            if (titleIndex != -1) {
                // Remove effects section
                int startIndex = titleIndex - 1;
                while (startIndex >= 0) {
                    Component line = existingLore.get(startIndex);
                    String linePlain = PLAIN_TEXT_SERIALIZER.serialize(line).trim();
                    if (!linePlain.isEmpty()) {
                        break;
                    }
                    startIndex--;
                }
                existingLore = new ArrayList<>(existingLore.subList(0, startIndex + 1));
            }
        }

        // Generate new effect lore
        List<Component> effectLoreLines = generateVanillaEffectLore(player, allEffects);

        // Combine clean lore + updated effect section
        List<Component> combinedLore = new ArrayList<>(existingLore);
        combinedLore.addAll(effectLoreLines);

        // Apply back to the item
        ItemUtil.setLore(item, combinedLore);

        Logger.debug("Updated vanilla item lore with {} effects", allEffects.size());
    }

    /**
     * Generates lore lines for effects on an item.
     *
     * @param baseEffects base effects (defined in item config)
     * @param additionalEffects additional effects (applied after item creation)
     * @param itemSettings the item's settings
     * @return list of lore components to add to the item
     */
    private List<Component> generateEffectLore(
            Player player,
            List<Effect> baseEffects,
            List<Effect> additionalEffects,
            ItemSettings itemSettings
    ) {
        // Determine how many effects to display
        int nbEffectsView = itemSettings.nbEffectsView();
       
        if (nbEffectsView == -1) {
            // Use global default if not specified per-item
            PluginSettings pluginSettings = Settings.get(PluginSettings.class);
            nbEffectsView = pluginSettings.defaultNbEffectsView();
        }

        // Collect visible effects based on settings
        List<Effect> visibleEffects = new ArrayList<>();

        if (itemSettings.baseEffectsVisible() && baseEffects != null) {
            visibleEffects.addAll(baseEffects);
        }

        if (itemSettings.additionalEffectsVisible() && additionalEffects != null) {
            visibleEffects.addAll(additionalEffects);
        }

        // Filter out effects without display names
        visibleEffects = visibleEffects.stream()
                .filter(effect -> effect.displayName() != null)
                .toList();

        return renderEffectLore(player, visibleEffects, nbEffectsView);
    }

    /**
     * Generates lore lines for base effects only (used during item creation).
     *
     * @param baseEffects base effects from item config
     * @param itemSettings the item's settings
     * @return list of lore components to add to the item
     */
    @Override
    public List<Component> generateBaseEffectLore(Player player, List<Effect> baseEffects, ItemSettings itemSettings) {
        return generateEffectLore(player, baseEffects, List.of(), itemSettings);
    }

    /**
     * Generates lore lines for vanilla items (all effects are treated as additional).
     *
     * @param allEffects all effects on the vanilla item
     * @return list of lore components to add to the item
     */
    private List<Component> generateVanillaEffectLore(Player player, List<Effect> allEffects) {
        // Get global default settings
        PluginSettings pluginSettings = Settings.get(PluginSettings.class);
        int nbEffectsView = pluginSettings.defaultNbEffectsView();

        // Filter out effects without display names
        List<Effect> visibleEffects = allEffects.stream()
                .filter(effect -> effect.displayName() != null)
                .toList();

        return renderEffectLore(player, visibleEffects, nbEffectsView);
    }

    /**
     * Renders effect lore lines from a list of visible effects.
     *
     * @param visibleEffects effects to display (already filtered)
     * @param nbEffectsView maximum number of effects to show (-1 for unlimited, 0 to hide all)
     * @return list of lore components
     */
    private List<Component> renderEffectLore(Player player, List<Effect> visibleEffects, int nbEffectsView) {
        List<Component> loreLines = new ArrayList<>();

        // If set to 0, don't show any effects
        if (nbEffectsView == 0 || visibleEffects.isEmpty()) {
            return loreLines;
        }

        // Add header (empty line) - only if not empty
        Component headerComponent = MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, Messages.EFFECTS_LORE_HEADER.get()));
        if (!headerComponent.equals(Component.empty())) {
            loreLines.add(headerComponent);
        }

        // Add title ("Effects")
        loreLines.add(MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, Messages.EFFECTS_LORE_TITLE.get())));

        // Add effect lines
        int effectsToShow = nbEffectsView == -1 ? visibleEffects.size() : Math.min(nbEffectsView, visibleEffects.size());

        for (int i = 0; i < effectsToShow; i++) {
            Effect effect = visibleEffects.get(i);
            Component effectLine = MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, Messages.EFFECTS_LORE_LINE.get()), Placeholder.component("effect", effect.displayName()));


            loreLines.add(effectLine);
        }

        // Add "And More..." if there are more effects than the limit
        if (nbEffectsView != -1 && visibleEffects.size() > nbEffectsView) {
            loreLines.add(MessageUtil.parseMessage(PlaceholderParser.parsePlaceholders(player, Messages.EFFECTS_LORE_MORE.get())));
        }

        return loreLines;
    }

    @Override
    public boolean isEffectItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        return Keys.EFFECT_REPRESENTATION.get(item.getItemMeta().getPersistentDataContainer()).isPresent();
    }

    @Override
    public Effect getEffectFromItem(ItemStack item) {
        if (!isEffectItem(item)) {
            return null;
        }

        String effectId = Keys.EFFECT_REPRESENTATION.get(item.getItemMeta().getPersistentDataContainer()).orElse(null);
        if (effectId == null) {
            return null;
        }

        return Registry.get(EffectsRegistry.class).getById(effectId);
    }

    @Override
    public ItemStack createEffectItem(Effect effect, Player player) {
        if (effect == null || effect.representation() == null) {
            return null;
        }

        EffectRepresentation representation = effect.representation();

        // Build the item from ItemStackWrapper
        ItemStack item = representation.item().build(player);

        // Store the effect ID in PDC to mark it as an effect representation
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            Keys.EFFECT_REPRESENTATION.set(container, effect.id());
            item.setItemMeta(meta);
        }

        return item;
    }

}
