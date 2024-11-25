package fr.maxlego08.items.api.buttons;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.zcore.utils.inventory.Pagination;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.button.ZButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ItemsButton extends ZButton implements PaginateButton {


    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        Pagination<Item> pagination = new Pagination<>();
        List<Item> items = JavaPlugin.getPlugin(ItemsPlugin.class).getItemManager().getItems();
        pagination.paginate(items, this.slots.size(), inventory.getPage());
        for (int i = 0; i != Math.min(items.size(), this.slots.size()); i++) {
            int slot = slots.get(i);
            ItemStack itemStack = items.get(i).build(player, 1);

            inventory.addItem(slot, itemStack).setClick(event -> {
                var rest = player.getInventory().addItem(itemStack);
                rest.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
            });
        }
    }

    @Override
    public int getPaginationSize(Player player) {
        return JavaPlugin.getPlugin(ItemsPlugin.class).getItemManager().getItems().size();
    }
}
