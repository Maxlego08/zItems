package fr.maxlego08.items.api.menus.buttons;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.CloneUtils;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.zcore.utils.inventory.Pagination;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ItemsButton extends ZButton implements PaginateButton {

    private final ItemsPlugin plugin;

    public ItemsButton(Plugin plugin) {
        this.plugin = (ItemsPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        Pagination<Item> pagination = new Pagination<>();
        List<Item> items = this.plugin.getItemManager().getItems();
        pagination.paginate(items, this.slots.size(), inventory.getPage());
        this.paginate(items, inventory, (slot, zItem) -> {
            ItemStack itemStack = zItem.build(player, 1);

            inventory.addItem(slot, itemStack).setClick(event -> {
                var rest = player.getInventory().addItem(CloneUtils.cloneItemStack(itemStack));
                rest.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
            });
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return this.plugin.getItemManager().getItems().size();
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
