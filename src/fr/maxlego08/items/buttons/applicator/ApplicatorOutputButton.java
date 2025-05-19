package fr.maxlego08.items.buttons.applicator;

import fr.maxlego08.items.api.CloneUtils;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.applicators.Applicator;
import fr.maxlego08.items.api.runes.exceptions.RuneException;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.SlotButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.recipes.impl.domains.ItemRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicatorOutputButton extends Button {


    private final ItemPlugin plugin;

    public ApplicatorOutputButton(Plugin plugin) {
        this.plugin = (ItemPlugin) plugin;
    }


    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        ItemRecipe itemRecipe = null;
        List<ItemStack> inputItems = getInputItems(inventory);
        ItemStack baseItem = getBaseItem(inventory);
        ItemStack runeItem = getRuneItem(inventory);
        List<ItemStack> extraInputItems = getExtraInputItems(inventory);
        ItemStack result = new ItemStack(Material.AIR);

        Rune rune = null;
        for (Applicator applicator : plugin.getRuneManager().getApplicators()) {
            if (applicator.canApply(baseItem, runeItem, inputItems, extraInputItems)) {
                itemRecipe = applicator.recipe();
                rune = applicator.rune();
                break;
            }
        }
        if (itemRecipe != null) {
            result = CloneUtils.cloneItemStack(baseItem.clone());
            try {
                this.plugin.getRuneManager().applyRune(result, rune);
                result.setAmount(itemRecipe.amount());
            } catch (RuneException exception) {
                result = new ItemStack(Material.AIR);
            }
        }

        inventory.addItem(this.slots.getFirst(), result).setClick(event -> this.onClick(event, inventory));
    }

    private List<ItemStack> getInputItems(InventoryEngine inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorInputButton)
                .flatMap(button -> button.getSlots().stream())
                .map(slot -> inventory.getInventory().getItem(slot))
                .filter(Objects::nonNull)
                .toList();
    }

    private ItemStack getBaseItem(InventoryEngine inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorBaseInputButton)
                .map(button -> {
                    var item = inventory.getInventory().getItem(new ArrayList<>(button.getSlots()).getFirst());
                    return item != null ? item : new ItemStack(Material.AIR);
                })
                .findFirst()
                .orElse(new ItemStack(Material.AIR));
    }

    private ItemStack getRuneItem(InventoryEngine inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorRuneInputButton)
                .map(button -> {
                    var item = inventory.getInventory().getItem(new ArrayList<>(button.getSlots()).getFirst());
                    return item != null ? item : new ItemStack(Material.AIR);
                })
                .findFirst()
                .orElse(new ItemStack(Material.AIR));
    }

    private List<ItemStack> getExtraInputItems(InventoryEngine inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorExtraInputButton)
                .flatMap(button -> button.getSlots().stream())
                .map(slot -> inventory.getInventory().getItem(slot))
                .filter(Objects::nonNull)
                .toList();
    }

    private void onClick(InventoryClickEvent event, InventoryEngine inventoryDefault) {
        event.setCancelled(true);
        if (event.getCurrentItem() != null && (event.getCursor().getType() == Material.AIR || event.getCursor().isSimilar(event.getCurrentItem()))) {
            if (event.getCursor().getType() == Material.AIR) {
                event.getWhoClicked().setItemOnCursor(CloneUtils.cloneItemStack(event.getCurrentItem()));
                event.setCurrentItem(new ItemStack(Material.AIR));
            } else {
                if (event.getCursor().getAmount() == event.getCursor().getMaxStackSize()) {
                    return;
                }
                int newAmount = Math.min(event.getCursor().getAmount() + event.getCurrentItem().getAmount(), event.getCursor().getMaxStackSize());
                int restForCurrentItem = event.getCurrentItem().getAmount() - (newAmount - event.getCursor().getAmount());
                event.getCursor().setAmount(newAmount);
                if (restForCurrentItem == 0) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                } else {
                    event.getCurrentItem().setAmount(restForCurrentItem);
                }
            }

            List<Integer> slots = new ArrayList<>();
            for (SlotButton button : inventoryDefault.getButtons()) {
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
}
