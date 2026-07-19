package fr.traqueur.items.menu;

import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.traqueur.items.menu.applicator.ApplicatorButton;
import fr.traqueur.items.api.utils.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Custom inventory for applying effects to items via GUI.
 * Players place a base item, an effect item, and required ingredients,
 * then retrieve the result with the effect applied.
 */
public class ApplicatorMenu extends ZInventory {

    public ApplicatorMenu(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
    }

    @Override
    public InventoryResult openInventory(Player player, InventoryEngine inventoryDefault) {
        // Allow clicking player inventory to move items in/out
        inventoryDefault.setDisablePlayerInventoryClick(false);
        return super.openInventory(player, inventoryDefault);
    }

    @Override
    public void closeInventory(Player player, InventoryEngine inventoryDefault) {
        // Return all items from input slots to player
        List<Button> buttons = inventoryDefault.getButtons().stream()
                .filter(button -> button instanceof ApplicatorButton)
                .toList();

        for (Button button : buttons) {
            for (int slot : button.getSlots()) {
                ItemStack item = inventoryDefault.getInventory().getItem(slot);
                if (item != null && !item.getType().isAir()) {
                    var rest = player.getInventory().addItem(ItemUtil.cloneItemStack(item));
                    rest.values().forEach(itemLeft -> player.getWorld().dropItem(player.getLocation(), itemLeft));
                }
            }
        }
    }
}