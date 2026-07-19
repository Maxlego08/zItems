package fr.traqueur.items.menu.applicator;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.Applicator;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.effects.EffectApplicationResult;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.registries.ApplicatorsRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Output button that displays the result of applying an effect to an item.
 * Validates the applicator recipe and creates the result item with the effect applied.
 */
public class ApplicatorOutputButton extends Button {

    private final ItemsPlugin plugin;

    public ApplicatorOutputButton(Plugin plugin) {
        this.plugin = (ItemsPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {
        ItemStack result = calculateResult(player, inventory);
        inventory.addItem(this.slots.getFirst(), result).setClick(event -> this.onClick(event, inventory));
    }

    private ItemStack calculateResult(Player player, InventoryEngine inventory) {
        EffectsManager effectsManager = plugin.getManager(EffectsManager.class);
        ApplicatorsRegistry applicatorsRegistry = Registry.get(ApplicatorsRegistry.class);

        if (effectsManager == null || applicatorsRegistry == null) {
            return new ItemStack(Material.AIR);
        }

        ItemStack baseItem = getBaseItem(inventory);
        ItemStack effectItem = getEffectItem(inventory);
        List<ItemStack> inputItems = getInputItems(inventory);

        // Validate effect item
        if (!effectsManager.isEffectItem(effectItem)) {
            return new ItemStack(Material.AIR);
        }

        Effect effect = effectsManager.getEffectFromItem(effectItem);
        if (effect == null) {
            return new ItemStack(Material.AIR);
        }

        // Get applicator for this effect
        Applicator applicator = applicatorsRegistry.getByEffect(effect);
        if (applicator == null) {
            Logger.debug("No applicator found for effect {}", effect.id());
            return new ItemStack(Material.AIR);
        }

        // Combine all inputs for validation
        List<ItemStack> allInputs = new ArrayList<>();
        allInputs.add(effectItem);
        allInputs.addAll(inputItems);

        // Validate and apply
        if (!applicator.canApply(baseItem, allInputs)) {
            return new ItemStack(Material.AIR);
        }

        return applyEffectToItem(player, baseItem, effect, effectsManager);
    }

    private ItemStack applyEffectToItem(Player player, ItemStack baseItem, Effect effect, EffectsManager effectsManager) {
        ItemStack result = baseItem.clone();
        EffectApplicationResult applicationResult = effectsManager.applyEffect(player, result, effect);

        if (applicationResult != EffectApplicationResult.SUCCESS) {
            Logger.debug("Failed to apply effect {}: {}", effect.id(), applicationResult);
            return new ItemStack(Material.AIR);
        }

        return result;
    }

    private List<ItemStack> getInputItems(InventoryEngine inventory) {
        return inventory.getButtons().stream()
                .filter(button -> button instanceof ApplicatorButton.Input)
                .flatMap(button -> button.getSlots().stream())
                .map(slot -> inventory.getInventory().getItem(slot))
                .filter(Objects::nonNull)
                .toList();
    }

    private ItemStack getBaseItem(InventoryEngine inventory) {
        return getItemFromButtonType(inventory, ApplicatorButton.BaseInput.class);
    }

    private ItemStack getEffectItem(InventoryEngine inventory) {
        return getItemFromButtonType(inventory, ApplicatorButton.EffectInput.class);
    }

    private <T extends ApplicatorButton> ItemStack getItemFromButtonType(InventoryEngine inventory, Class<T> buttonClass) {
        return inventory.getButtons().stream()
                .filter(buttonClass::isInstance)
                .map(button -> {
                    var item = inventory.getInventory().getItem(new ArrayList<>(button.getSlots()).getFirst());
                    return item != null ? item : new ItemStack(Material.AIR);
                })
                .findFirst()
                .orElse(new ItemStack(Material.AIR));
    }


    private void onClick(InventoryClickEvent event, InventoryEngine inventoryDefault) {
        event.setCancelled(true);
        if (!canTakeOutput(event)) {
            return;
        }

        handleOutputRetrieval(event);
        consumeInputs(inventoryDefault);
    }

    private boolean canTakeOutput(InventoryClickEvent event) {
        return event.getCurrentItem() != null
                && !event.getCurrentItem().getType().isAir()
                && (event.getCursor().getType() == Material.AIR || event.getCursor().isSimilar(event.getCurrentItem()));
    }

    private void handleOutputRetrieval(InventoryClickEvent event) {
        if (event.getCursor().getType() == Material.AIR) {
            event.getWhoClicked().setItemOnCursor(ItemUtil.cloneItemStack(event.getCurrentItem()));
            event.setCurrentItem(new ItemStack(Material.AIR));
        } else {
            if (event.getCursor().getAmount() == event.getCursor().getMaxStackSize()) {
                return;
            }
            int newAmount = Math.min(event.getCursor().getAmount() + event.getCurrentItem().getAmount(),
                    event.getCursor().getMaxStackSize());
            int restForCurrentItem = event.getCurrentItem().getAmount() - (newAmount - event.getCursor().getAmount());
            event.getCursor().setAmount(newAmount);
            if (restForCurrentItem == 0) {
                event.setCurrentItem(new ItemStack(Material.AIR));
            } else {
                event.getCurrentItem().setAmount(restForCurrentItem);
            }
        }
    }

    private void consumeInputs(InventoryEngine inventoryDefault) {
        List<Integer> slots = new ArrayList<>();
        for (var button : inventoryDefault.getButtons()) {
            if (button instanceof ApplicatorButton) {
                slots.addAll(button.getSlots());
            }
        }
        slots.forEach(slot -> {
            ItemStack item = inventoryDefault.getInventory().getItem(slot);
            if (item == null) {
                return;
            }
            if (item.getAmount() == 1) {
                inventoryDefault.getInventory().setItem(slot, new ItemStack(Material.AIR));
            } else {
                item.setAmount(item.getAmount() - 1);
                inventoryDefault.getInventory().setItem(slot, item);
            }
        });
    }
}