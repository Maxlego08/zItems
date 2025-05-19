package fr.maxlego08.items.buttons.applicator;

import fr.maxlego08.items.api.CloneUtils;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public abstract class ApplicatorButton extends Button {


    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventoryEngine) {
        for (int slot : this.slots) {
            inventoryEngine.addItem(slot, new ItemStack(Material.AIR)).setClick(event -> this.onClick(event, inventoryEngine));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, Player player, InventoryEngine inventoryDefault) {
        super.onInventoryClick(event, player, inventoryDefault);
        var inventory = event.getClickedInventory();
        if (inventory != null && inventory.getType() == InventoryType.PLAYER && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
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
            case LEFT -> {
                this.leftClick((Player) event.getWhoClicked(), inventoryDefault, event);
            }
            case RIGHT -> {
                this.rightClick((Player) event.getWhoClicked(), inventoryDefault, event);
            }
            default -> {
                return;
            }
        }
        inventoryDefault.getButtons().stream().filter(button -> button instanceof ApplicatorOutputButton).forEach(button -> {
            button.onRender((Player) event.getWhoClicked(), inventoryDefault);
        });
    }

    private void leftClick(Player player, InventoryEngine inventory, InventoryClickEvent event) {
        switch (event.getAction()) {
            case PLACE_ALL, PLACE_SOME -> {
                int maxStackSize = event.getCurrentItem() == null ? event.getCursor().getMaxStackSize() : event.getCurrentItem().getMaxStackSize();
                int currentAmount = event.getCurrentItem() == null ? 0 : event.getCurrentItem().getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + event.getCursor().getAmount());
                place(player, inventory, event, currentAmount, newAmount);
            }
            case SWAP_WITH_CURSOR -> {
                swap(player, inventory, event);
            }
            case PICKUP_ALL -> {
                player.setItemOnCursor(CloneUtils.cloneItemStack(event.getCurrentItem()));
                inventory.addItem(event.getRawSlot(), new ItemStack(Material.AIR)).setClick(event1 -> this.onClick(event1, inventory));
            }
        }
    }

    private void place(Player player, InventoryEngine inventoryDefault, InventoryClickEvent event, int currentAmount, int newAmount) {
        int rest = player.getItemOnCursor().getAmount() - (newAmount - currentAmount);
        ItemStack item = CloneUtils.cloneItemStack(player.getItemOnCursor());
        ItemStack cursor = new ItemStack(Material.AIR);
        item.setAmount(newAmount);
        inventoryDefault.addItem(event.getRawSlot(), item).setClick(event1 -> this.onClick(event1, inventoryDefault));
        if (rest > 0) {
            cursor = CloneUtils.cloneItemStack(player.getItemOnCursor());
            cursor.setAmount(rest);
        }
        player.setItemOnCursor(cursor);
    }

    private void rightClick(Player player, InventoryEngine inventory, InventoryClickEvent event) {
        switch (event.getAction()) {
            case SWAP_WITH_CURSOR -> {
                swap(player, inventory, event);
            }
            case PLACE_ONE -> {
                int maxStackSize = event.getCurrentItem() == null ? event.getCursor().getMaxStackSize() : event.getCurrentItem().getMaxStackSize();
                int currentAmount = event.getCurrentItem() == null ? 0 : event.getCurrentItem().getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + 1);
                place(player, inventory, event, currentAmount, newAmount);
            }
            case PICKUP_HALF -> {
                int half = event.getCurrentItem().getAmount() / 2;
                if (event.getCurrentItem().getAmount() == 1) {
                    half = 1;
                }
                ItemStack item = CloneUtils.cloneItemStack(event.getCurrentItem());
                item.setAmount(half);
                player.setItemOnCursor(item);
                item.setAmount(event.getCurrentItem().getAmount() - half);
                inventory.addItem(event.getRawSlot(), item).setClick(event1 -> this.onClick(event1, inventory));
            }
        }
    }

    private void swap(Player player, InventoryEngine inventory, InventoryClickEvent event) {
        ItemStack cursor = CloneUtils.cloneItemStack(event.getCursor());
        ItemStack current = CloneUtils.cloneItemStack(event.getCurrentItem());
        if (current != null && current.isSimilar(cursor)) {
            int maxStackSize = current.getMaxStackSize();
            int currentAmount = current.getAmount();
            int newAmount = Math.min(maxStackSize, currentAmount + cursor.getAmount());
            place(player, inventory, event, currentAmount, newAmount);
        } else {
            player.setItemOnCursor(current);
            inventory.addItem(event.getRawSlot(), cursor).setClick(event1 -> this.onClick(event1, inventory));
        }
    }
}
