package fr.maxlego08.items.buttons;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class ConfirmButton extends ZButton {

    private final ItemsPlugin plugin;

    public ConfirmButton(Plugin plugin) {
        this.plugin = (ItemsPlugin) plugin;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        plugin.getCommandsListener().confirm(player, Action.RIGHT_CLICK_BLOCK, false);
    }
}
