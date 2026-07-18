package fr.traqueur.items.menu;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.traqueur.items.Messages;
import fr.traqueur.items.api.ItemsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

/**
 * Custom back button that pops the navigation stack and restores the previous folder state.
 *
 * <p>Overrides {@code onClick} (not {@code onInventoryClick}). The {@code onInventoryClick}
 * method is broadcast to ALL buttons on every click — overriding it would cancel every click
 * and pop the stack on every player action. The {@code onClick} method is called only when
 * this specific button's slot is clicked.</p>
 */
public class ZItemsBackButton extends Button {

    private final ItemsPlugin plugin;

    public ZItemsBackButton(Plugin plugin) {
        this.plugin = (ItemsPlugin) plugin;
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {
        boolean restored = ItemsListButton.popAndRestoreState(player, plugin);
        if (restored) {
            var invManager = plugin.getInventoryManager();
            invManager.getInventory(plugin, "items_list").ifPresentOrElse(
                    inv -> invManager.openInventoryWithOldInventories(player, inv, 1),
                    () -> Messages.FAILED_TO_OPEN_GUI.send(player)
            );
        }
    }
}