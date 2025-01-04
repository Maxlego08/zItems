package fr.maxlego08.items.api.menus.buttons;

import fr.maxlego08.items.api.CloneUtils;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class ApplicatorButton extends ZButton {


    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        for (int slot : this.slots) {
            inventory.addItem(slot, new ItemStack(Material.AIR)).setClick(event -> this.onClick(event, inventory));
        }
    }

    private void onClick(InventoryClickEvent event, InventoryDefault inventoryDefault) {
        event.setCancelled(true);
        if(event.getRawSlot() >= event.getInventory().getSize()) {
            return;
        }
        if(!this.slots.contains(event.getSlot())) {
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

    private void leftClick(Player player, InventoryDefault inventory, InventoryClickEvent event) {
        switch (event.getAction()) {
            case PLACE_ALL, PLACE_SOME -> {
                int maxStackSize = event.getCurrentItem() == null ? event.getCursor().getMaxStackSize() : event.getCurrentItem().getMaxStackSize();
                int currentAmount = event.getCurrentItem() == null ? 0 : event.getCurrentItem().getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + event.getCursor().getAmount());
                place(player, inventory,event, currentAmount, newAmount);
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

    private void place(Player player, InventoryDefault inventoryDefault, InventoryClickEvent event, int currentAmount, int newAmount) {
        int rest = player.getItemOnCursor().getAmount() - (newAmount - currentAmount);
        ItemStack item = CloneUtils.cloneItemStack(player.getItemOnCursor());
        ItemStack cursor = new ItemStack(Material.AIR);
        item.setAmount(newAmount);
        inventoryDefault.addItem(event.getRawSlot(), item).setClick(event1 -> this.onClick(event1, inventoryDefault));
        if(rest > 0) {
            cursor = CloneUtils.cloneItemStack(player.getItemOnCursor());
            cursor.setAmount(rest);
        }
        player.setItemOnCursor(cursor);
    }

    private void rightClick(Player player, InventoryDefault inventory, InventoryClickEvent event) {
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
                if(event.getCurrentItem().getAmount() == 1) {
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

    private void swap(Player player, InventoryDefault inventory, InventoryClickEvent event) {
        ItemStack cursor = CloneUtils.cloneItemStack(event.getCursor());
        ItemStack current = CloneUtils.cloneItemStack(event.getCurrentItem());
        if(current != null && current.isSimilar(cursor)) {
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
