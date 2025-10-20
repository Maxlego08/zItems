package fr.maxlego08.items.inventories;

import fr.maxlego08.items.utils.CloneUtils;
import fr.maxlego08.items.buttons.applicator.ApplicatorBaseInputButton;
import fr.maxlego08.items.buttons.applicator.ApplicatorExtraInputButton;
import fr.maxlego08.items.buttons.applicator.ApplicatorInputButton;
import fr.maxlego08.items.buttons.applicator.ApplicatorRuneInputButton;
import fr.maxlego08.menu.ZInventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ApplicatorMenu extends ZInventory {

    public ApplicatorMenu(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super(plugin, name, fileName, size, buttons);
    }

    @Override
    public InventoryResult openInventory(Player player, InventoryEngine inventoryDefault) {
        inventoryDefault.setDisablePlayerInventoryClick(false);
        return super.openInventory(player, inventoryDefault);
    }

    @Override
    public void closeInventory(Player player, InventoryEngine inventoryDefault) {
        List<Button> buttons = inventoryDefault.getButtons().stream().filter(button -> button instanceof ApplicatorInputButton || button instanceof ApplicatorExtraInputButton || button instanceof ApplicatorBaseInputButton || button instanceof ApplicatorRuneInputButton).toList();
        for (Button button : buttons) {
            for (int slot : button.getSlots()) {
                ItemStack item = inventoryDefault.getInventory().getItem(slot);
                if (item != null && !item.getType().isAir()) {
                    var rest = player.getInventory().addItem(CloneUtils.cloneItemStack(item));
                    rest.values().forEach(itemLeft -> player.getWorld().dropItem(player.getLocation(), itemLeft));
                }
            }
        }
    }
}
