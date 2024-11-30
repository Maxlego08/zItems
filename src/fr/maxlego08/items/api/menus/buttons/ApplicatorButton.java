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
                place(player, event, currentAmount, newAmount);
            }
            case SWAP_WITH_CURSOR -> {
                ItemStack item = CloneUtils.cloneItemStack(event.getCursor());
                player.setItemOnCursor(CloneUtils.cloneItemStack(event.getCurrentItem()));
                event.getInventory().setItem(event.getRawSlot(), item);
            }
            case PICKUP_ALL -> {
                player.setItemOnCursor(CloneUtils.cloneItemStack(event.getCurrentItem()));
                event.getInventory().setItem(event.getRawSlot(), new ItemStack(Material.AIR));
            }
        }
    }

    private void place(Player player, InventoryClickEvent event, int currentAmount, int newAmount) {
        int rest = event.getCursor().getAmount() - (newAmount - currentAmount);
        ItemStack item = CloneUtils.cloneItemStack(event.getCursor());
        item.setAmount(newAmount);
        event.getInventory().setItem(event.getRawSlot(), item);
        if(rest > 0) {
            event.getCursor().setAmount(rest);
        } else {
            player.setItemOnCursor(new ItemStack(Material.AIR));
        }
    }

    private void rightClick(Player player, InventoryDefault inventory, InventoryClickEvent event) {
        switch (event.getAction()) {
            case SWAP_WITH_CURSOR -> {
                ItemStack item = CloneUtils.cloneItemStack(event.getCursor());
                player.setItemOnCursor(CloneUtils.cloneItemStack(event.getCurrentItem()));
                event.getInventory().setItem(event.getRawSlot(), item);
            }
            case PLACE_ONE -> {
                int maxStackSize = event.getCurrentItem() == null ? event.getCursor().getMaxStackSize() : event.getCurrentItem().getMaxStackSize();
                int currentAmount = event.getCurrentItem() == null ? 0 : event.getCurrentItem().getAmount();
                int newAmount = Math.min(maxStackSize, currentAmount + 1);
                place(player, event, currentAmount, newAmount);
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
                event.getInventory().setItem(event.getRawSlot(), item);
            }
        }
    }



}
