package fr.traqueur.items.menu.applicator;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Base class for applicator input buttons.
 * Handles placing and removing items from input slots.
 */
public abstract sealed class ApplicatorButton extends Button
        permits ApplicatorButton.Input,
                ApplicatorButton.BaseInput,
                ApplicatorButton.EffectInput {

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventoryEngine) {
        for (int slot : this.slots) {
            inventoryEngine.addItem(slot, new ItemStack(Material.AIR))
                    .setClick(event -> this.onClick(event, inventoryEngine));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, Player player, InventoryEngine inventoryDefault) {
        super.onInventoryClick(event, player, inventoryDefault);
        var inventory = event.getClickedInventory();
        if (inventory != null && inventory.getType() == InventoryType.PLAYER
                && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }

    private void onClick(InventoryClickEvent event, InventoryEngine inventoryDefault) {
        event.setCancelled(true);
        if (event.getRawSlot() >= event.getInventory().getSize()) {
            return;
        }
        if (!this.slots.contains(event.getSlot())) {
            return;
        }

        switch (event.getClick()) {
            case LEFT -> leftClick((Player) event.getWhoClicked(), inventoryDefault, event);
            case RIGHT -> rightClick((Player) event.getWhoClicked(), inventoryDefault, event);
            default -> {
                return;
            }
        }

        // Update output button after each change
        inventoryDefault.getButtons().stream()
                .filter(button -> button instanceof ApplicatorOutputButton)
                .forEach(button -> button.onRender((Player) event.getWhoClicked(), inventoryDefault));
    }

    private void leftClick(Player player, InventoryEngine inventory, InventoryClickEvent event) {
        switch (event.getAction()) {
            case PLACE_ALL, PLACE_SOME -> {
                int maxStackSize = event.getCurrentItem() == null ? event.getCursor().getMaxStackSize()
                        : event.getCurrentItem().getMaxStackSize();
                int currentAmount = event.getCurrentItem() == null ? 0 : event.getCurrentItem().getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + event.getCursor().getAmount());
                place(player, inventory, event, currentAmount, newAmount);
            }
            case SWAP_WITH_CURSOR -> swap(player, inventory, event);
            case PICKUP_ALL -> {
                player.setItemOnCursor(ItemUtil.cloneItemStack(event.getCurrentItem()));
                inventory.addItem(event.getRawSlot(), new ItemStack(Material.AIR))
                        .setClick(event1 -> this.onClick(event1, inventory));
            }
        }
    }

    private void place(Player player, InventoryEngine inventoryDefault, InventoryClickEvent event,
                      int currentAmount, int newAmount) {
        int rest = player.getItemOnCursor().getAmount() - (newAmount - currentAmount);
        ItemStack item = player.getItemOnCursor().clone();
        ItemStack cursor = new ItemStack(Material.AIR);
        item.setAmount(newAmount);
        inventoryDefault.addItem(event.getRawSlot(), item)
                .setClick(event1 -> this.onClick(event1, inventoryDefault));
        if (rest > 0) {
            cursor = player.getItemOnCursor().clone();
            cursor.setAmount(rest);
        }
        player.setItemOnCursor(cursor);
    }

    private void rightClick(Player player, InventoryEngine inventory, InventoryClickEvent event) {
        switch (event.getAction()) {
            case SWAP_WITH_CURSOR -> swap(player, inventory, event);
            case PLACE_ONE -> {
                int maxStackSize = event.getCurrentItem() == null ? event.getCursor().getMaxStackSize()
                        : event.getCurrentItem().getMaxStackSize();
                int currentAmount = event.getCurrentItem() == null ? 0 : event.getCurrentItem().getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + 1);
                place(player, inventory, event, currentAmount, newAmount);
            }
            case PICKUP_HALF -> {
                int half = event.getCurrentItem().getAmount() / 2;
                if (event.getCurrentItem().getAmount() == 1) {
                    half = 1;
                }
                ItemStack item = event.getCurrentItem().clone();
                item.setAmount(half);
                player.setItemOnCursor(ItemUtil.cloneItemStack(item));
                item.setAmount(event.getCurrentItem().getAmount() - half);
                inventory.addItem(event.getRawSlot(), item)
                        .setClick(event1 -> this.onClick(event1, inventory));
            }
        }
    }

    private void swap(Player player, InventoryEngine inventory, InventoryClickEvent event) {
        ItemStack cursor = ItemUtil.cloneItemStack(event.getCursor());
        ItemStack current = event.getCurrentItem();
        if(current == null) {
            current = new ItemStack(Material.AIR);
        }
        current = ItemUtil.cloneItemStack(current);
        if (current.isSimilar(cursor)) {
            int maxStackSize = current.getMaxStackSize();
            int currentAmount = current.getAmount();
            int newAmount = Math.min(maxStackSize, currentAmount + cursor.getAmount());
            place(player, inventory, event, currentAmount, newAmount);
        } else {
            player.setItemOnCursor(current);
            inventory.addItem(event.getRawSlot(), cursor)
                    .setClick(event1 -> this.onClick(event1, inventory));
        }
    }

    /**
     * Button for input slots in the applicator.
     * Used for required ingredients (from EffectRepresentation.ingredients).
     */
    public static final class Input extends ApplicatorButton {
    }

    /**
     * Button for the base item slot in the applicator.
     * This is the item that will receive the effect.
     */
    public static final class BaseInput extends ApplicatorButton {
    }

    /**
     * Button for the effect item slot in the applicator.
     * Players place an effect representation item here.
     */
    public static final class EffectInput extends ApplicatorButton {
    }
}