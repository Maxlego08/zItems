package fr.maxlego08.items.api.menus.buttons;

import fr.maxlego08.items.api.CloneUtils;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.applicators.Applicator;
import fr.maxlego08.items.api.runes.exceptions.RuneException;
import fr.maxlego08.menu.api.button.SlotButton;
import fr.maxlego08.menu.button.ZButton;
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

public class ApplicatorOutputButton extends ZButton {


    private final ItemPlugin plugin;

    public ApplicatorOutputButton(Plugin plugin) {
        this.plugin = (ItemPlugin) plugin;
    }


    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        ItemRecipe itemRecipe = null;
        List<ItemStack> inputItems = getInputItems(inventory);
        ItemStack baseItem = getBaseItem(inventory);
        List<ItemStack> extraInputItems = getExtraInputItems(inventory);
        Rune rune = null;
        for (Applicator applicator : plugin.getRuneManager().getApplicators()) {
            if(applicator.canApply(baseItem, inputItems, extraInputItems)) {
                itemRecipe = applicator.recipe();
                rune = applicator.rune();
                break;
            }
        }
        ItemStack result = new ItemStack(Material.AIR);
        if (itemRecipe != null) {
            result = CloneUtils.cloneItemStack(baseItem.clone());
            try {
                this.plugin.getRuneManager().applyRune(result, rune);
            } catch (RuneException e) {
                throw new RuntimeException(e);
            }
        }
        result.setAmount(itemRecipe == null ? 1 : itemRecipe.amount());

        inventory.addItem(this.slots.getFirst(), result).setClick(event -> this.onClick(event, inventory));
    }

    private List<ItemStack> getInputItems(InventoryDefault inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorInputButton)
                .flatMap(button -> button.getSlots().stream())
                .map(slot -> inventory.getInventory().getItem(slot))
                .filter(Objects::nonNull)
                .toList();
    }

    private ItemStack getBaseItem(InventoryDefault inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorBaseInputButton)
                .map(button -> inventory.getInventory().getItem(new ArrayList<>(button.getSlots()).getFirst()) == null
                        ? new ItemStack(Material.AIR)
                        : inventory.getInventory().getItem(new ArrayList<>(button.getSlots()).getFirst()))
                .findFirst()
                .orElse(new ItemStack(Material.AIR));
    }

    private List<ItemStack> getExtraInputItems(InventoryDefault inventory) {
        return inventory.getButtons()
                .stream()
                .filter(button -> button instanceof ApplicatorExtraInputButton)
                .flatMap(button -> button.getSlots().stream())
                .map(slot -> inventory.getInventory().getItem(slot))
                .filter(Objects::nonNull)
                .toList();
    }

    private void onClick(InventoryClickEvent event, InventoryDefault inventoryDefault) {
        event.setCancelled(true);
        if(event.getCurrentItem() != null && (event.getCursor().getType() == Material.AIR || event.getCursor().isSimilar(event.getCurrentItem())) ) {
            if(event.getCursor().getType() == Material.AIR) {
                event.getWhoClicked().setItemOnCursor(CloneUtils.cloneItemStack(event.getCurrentItem()));
                event.setCurrentItem(new ItemStack(Material.AIR));
            } else {
                if(event.getCursor().getAmount() == event.getCursor().getMaxStackSize()) {
                    return;
                }
                int newAmount = Math.min(event.getCursor().getAmount() + event.getCurrentItem().getAmount(), event.getCursor().getMaxStackSize());
                int restForCurrentItem = event.getCurrentItem().getAmount() - (newAmount - event.getCursor().getAmount());
                event.getCursor().setAmount(newAmount);
                if(restForCurrentItem == 0) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                } else {
                    event.getCurrentItem().setAmount(restForCurrentItem);
                }
            }

            List<Integer> slots = new ArrayList<>();
            for (SlotButton button : inventoryDefault.getButtons()) {
                if(button instanceof ApplicatorButton) {
                    slots.addAll(button.getSlots());
                }
            }
            slots.forEach(slot -> {
                ItemStack item = inventoryDefault.getInventory().getItem(slot);
                if(item.getAmount() == 1) {
                    inventoryDefault.getInventory().setItem(slot, new ItemStack(Material.AIR));
                } else {
                    item.setAmount(item.getAmount() - 1);
                    inventoryDefault.getInventory().setItem(slot, item);
                }
            });
        }
    }
}
