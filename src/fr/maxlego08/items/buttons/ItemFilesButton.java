package fr.maxlego08.items.buttons;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.CloneUtils;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.utils.ItemFile;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemFilesButton extends PaginateButton {

    private final ItemsPlugin plugin;

    public ItemFilesButton(Plugin plugin) {
        this.plugin = (ItemsPlugin) plugin;
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventory) {

        var elements = getElements(player);
        var manager = this.plugin.getItemManager();

        paginate(elements, inventory, (slot, element) -> {

            if (element.isDirectory()) {

                Placeholders placeholders = new Placeholders();
                placeholders.register("name", element.itemFile.name());
                placeholders.register("material", element.itemFile.displayMaterial().name());
                placeholders.register("model-id", String.valueOf(element.itemFile.displayModelId()));

                inventory.addItem(slot, getItemStack().build(player, false, placeholders)).setClick(event -> {
                    player.setMetadata("zitems-files", new FixedMetadataValue(this.plugin, element.itemFile));
                    var inventoryManager = plugin.getInventoryManager();

                    inventoryManager.getInventory(plugin, "items_folders").ifPresentOrElse(inv -> {
                        inventoryManager.openInventoryWithOldInventories(player, inv, 1);
                    }, () -> player.sendMessage("§cImpossible to find the inventory !"));
                });

            } else {
                ItemStack itemStack = element.item.build(player, 1);

                inventory.addItem(slot, itemStack).setClick(event -> {
                    var rest = player.getInventory().addItem(CloneUtils.cloneItemStack(itemStack));
                    rest.values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                });
            }
        });
    }

    @Override
    public int getPaginationSize(Player player) {
        return getElements(player).size();
    }

    private List<Element> getElements(Player player) {

        ItemFile itemFile = this.plugin.getItemManager().getItemFile();
        if (player.hasMetadata("zitems-files")) {
            itemFile = (ItemFile) player.getMetadata("zitems-files").getFirst().value();
        }

        if (itemFile == null) return new ArrayList<>();

        var manager = this.plugin.getItemManager();

        List<Element> elements = new ArrayList<>();
        itemFile.itemFiles().forEach(e -> elements.add(new Element(null, e)));
        itemFile.files().stream().map(manager::getItem).filter(Optional::isPresent).map(Optional::get).forEach(e -> elements.add(new Element(e, null)));
        return elements;
    }

    @Override
    public void onInventoryClose(Player player, InventoryEngine inventory) {
        player.removeMetadata("zitems-files", this.plugin);
    }

    public record Element(Item item, ItemFile itemFile) {

        public boolean isDirectory() {
            return itemFile != null;
        }

    }
}
