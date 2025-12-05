package fr.traqueur.items.listeners;

import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectHandler;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.registries.HandlersRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.effects.handlers.EnchantsApplicator;
import fr.traqueur.items.effects.settings.EnchantsSettings;
import fr.traqueur.items.serialization.Keys;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Listener responsible for handling effect fusion in anvils.
 * <p>
 * This listener manages:
 * - Effect incompatibility checks (blocks fusion if incompatible effects are present)
 * - Effect merging from both items onto the result
 * - Special handling for ENCHANTS_APPLICATOR effects (treats enchantments as bonuses)
 * <p>
 * Example: Pickaxe Effi 2 with ENCHANTS_APPLICATOR +1 (displays Effi 3) fused with Effi 3 pickaxe
 * → Result: Effi 3 base + ENCHANTS_APPLICATOR +1 = Effi 4 displayed
 */
public class AnvilEffectFusionListener implements Listener {

    private final ItemsPlugin plugin;

    public AnvilEffectFusionListener(ItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        // If we don't have two items, do nothing
        if (firstItem == null || secondItem == null || firstItem.getType().isAir() || secondItem.getType().isAir()) {
            return;
        }

        // Check if second item is an enchanted book via meta
        boolean isEnchantedBook = secondItem.hasItemMeta() &&
                                  secondItem.getItemMeta() instanceof EnchantmentStorageMeta;

        // Only handle items of the same type OR item + enchanted book (vanilla anvil logic)
        if (firstItem.getType() != secondItem.getType() && !isEnchantedBook) {
            return;
        }

        // Get effects from both items
        List<Effect> effects1 = Keys.EFFECTS.get(
                firstItem.getItemMeta().getPersistentDataContainer(),
                new ArrayList<>()
        );
        List<Effect> effects2 = Keys.EFFECTS.get(
                secondItem.getItemMeta().getPersistentDataContainer(),
                new ArrayList<>()
        );

        // If neither item has effects and it's not an enchanted book fusion, do nothing
        if (effects1.isEmpty() && effects2.isEmpty() && !isEnchantedBook) {
            return;
        }

        // If only the first item has effects and second is enchanted book, we need to handle this
        if (!effects1.isEmpty() && isEnchantedBook) {
            // Continue to handle the fusion with effects
        } else if (effects1.isEmpty() && effects2.isEmpty()) {
            // No effects on either side, let vanilla handle it
            return;
        }

        // Check for incompatibilities between the two items
        HandlersRegistry registry = Registry.get(HandlersRegistry.class);
        for (Effect effect1 : effects1) {
            EffectHandler<?> handler1 = registry.getById(effect1.type());
            if (handler1 == null) continue;

            for (Effect effect2 : effects2) {
                EffectHandler<?> handler2 = registry.getById(effect2.type());
                if (handler2 == null) continue;

                if (areEffectsIncompatible(handler1, handler2)) {
                    // Block the fusion
                    Logger.debug("Blocking anvil fusion due to incompatible effects: {} and {}",
                            effect1.type(), effect2.type());
                    event.setResult(null);
                    return;
                }
            }
        }

        // Start with the FIRST item as base (preserves all effects and their formatting)
        ItemStack result = firstItem.clone();
        ItemMeta resultMeta = result.getItemMeta();
        if (resultMeta == null) {
            return;
        }

        // Calculate base enchantments (without ENCHANTS_APPLICATOR bonuses)
        Map<Enchantment, Integer> baseEnchants1 = calculateBaseEnchantments(firstItem, effects1, registry);
        Map<Enchantment, Integer> baseEnchants2 = calculateBaseEnchantments(secondItem, effects2, registry);

        // Merge base enchantments (vanilla anvil logic)
        Map<Enchantment, Integer> mergedEnchants = mergeEnchantments(baseEnchants1, baseEnchants2);

        // Remove all enchantments (including ENCHANTS_APPLICATOR bonuses)
        for (Enchantment enchant : new HashSet<>(resultMeta.getEnchants().keySet())) {
            resultMeta.removeEnchant(enchant);
        }

        // Apply merged base enchantments
        for (Map.Entry<Enchantment, Integer> entry : mergedEnchants.entrySet()) {
            resultMeta.addEnchant(entry.getKey(), entry.getValue(), true);
        }

        result.setItemMeta(resultMeta);

        // Update PDC with all effects (first item + new effects from second item)
        List<Effect> allEffects = new ArrayList<>(effects1);
        for (Effect effect2 : effects2) {
            if (effects1.stream().noneMatch(e -> e.id().equals(effect2.id()))) {
                allEffects.add(effect2);
            }
        }

        Keys.EFFECTS.set(result.getItemMeta().getPersistentDataContainer(), allEffects);

        // Reapply all NoEventEffects (from both items) using the manager
        EffectsManager effectsManager = plugin.getManager(EffectsManager.class);
        if (effectsManager != null) {
            effectsManager.reapplyNoEventEffects(null, result, allEffects);

            // Update item lore to show all effects using the manager
            effectsManager.updateItemLoreWithEffects((Player) event.getViewers().getFirst(), result, allEffects);
        }

        event.setResult(result);
        Logger.debug("Anvil fusion completed: merged {} effects from both items", allEffects.size());
    }

    /**
     * Checks if two effect handlers are incompatible with each other.
     */
    private boolean areEffectsIncompatible(EffectHandler<?> handler1, EffectHandler<?> handler2) {
        return handler1.getIncompatibleHandlers().contains(handler2.getClass()) ||
               handler2.getIncompatibleHandlers().contains(handler1.getClass());
    }

    /**
     * Calculates the base enchantments of an item without ENCHANTS_APPLICATOR bonuses.
     * This extracts the "real" enchantments by removing the bonus levels added by effects.
     * Also handles enchanted books which store enchantments in EnchantmentStorageMeta.
     *
     * Example: Item has Effi 3 displayed, with ENCHANTS_APPLICATOR +1 effect
     * → Base enchantment is Effi 2
     */
    private Map<Enchantment, Integer> calculateBaseEnchantments(ItemStack item, List<Effect> effects, HandlersRegistry registry) {
        if (!item.hasItemMeta()) {
            return new HashMap<>();
        }

        ItemMeta meta = item.getItemMeta();
        Map<Enchantment, Integer> enchants;

        // Check if this is an enchanted book (uses EnchantmentStorageMeta)
        if (meta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            enchants = new HashMap<>(enchantmentStorageMeta.getStoredEnchants());
        } else {
            enchants = new HashMap<>(meta.getEnchants());
        }

        // Remove ENCHANTS_APPLICATOR bonuses to get base enchantments
        for (Effect effect : effects) {
            EffectHandler<?> handler = registry.getById(effect.type());
            if (handler instanceof EnchantsApplicator &&
                    effect.settings() instanceof EnchantsSettings(
                            List<EnchantsSettings.EnchantSetting> enchantments,
                            List<org.bukkit.Material> applicableMaterials,
                            List<org.bukkit.Tag<org.bukkit.Material>> applicableTags,
                            boolean applicabilityBlacklisted
                    )) {

                for (EnchantsSettings.EnchantSetting enchantSetting : enchantments) {
                    Enchantment enchant = enchantSetting.wrapper().enchantment();
                    int evolutionValue = enchantSetting.computeEvolutionValue();

                    if (enchants.containsKey(enchant)) {
                        int currentLevel = enchants.get(enchant);
                        int baseLevel = currentLevel - evolutionValue;

                        if (baseLevel <= 0) {
                            enchants.remove(enchant);
                        } else {
                            enchants.put(enchant, baseLevel);
                        }
                    }
                }
            }
        }

        return enchants;
    }

    /**
     * Merges two enchantment maps using vanilla anvil logic.
     * - Same enchant, same level → level + 1 (if not exceeding max)
     * - Same enchant, different level → higher level
     * - Different enchants → both included (if compatible)
     */
    private Map<Enchantment, Integer> mergeEnchantments(Map<Enchantment, Integer> enchants1, Map<Enchantment, Integer> enchants2) {
        Map<Enchantment, Integer> merged = new HashMap<>(enchants1);

        for (Map.Entry<Enchantment, Integer> entry : enchants2.entrySet()) {
            Enchantment enchant = entry.getKey();
            int level2 = entry.getValue();

            if (merged.containsKey(enchant)) {
                int level1 = merged.get(enchant);

                if (level1 == level2) {
                    // Same level: upgrade by 1 if possible (vanilla anvil behavior)
                    int newLevel = Math.min(level1 + 1, enchant.getMaxLevel());
                    merged.put(enchant, newLevel);
                } else {
                    // Different levels: take the higher one
                    merged.put(enchant, Math.max(level1, level2));
                }
            } else {
                // Enchantment only on second item: add it if compatible
                boolean compatible = true;
                for (Enchantment existingEnchant : merged.keySet()) {
                    if (enchant.conflictsWith(existingEnchant)) {
                        compatible = false;
                        break;
                    }
                }

                if (compatible) {
                    merged.put(enchant, level2);
                }
            }
        }

        return merged;
    }
}