package fr.maxlego08.items;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.command.commands.CommandItem;
import fr.maxlego08.items.components.PaperComponent;
import fr.maxlego08.items.components.SpigotComponent;
import fr.maxlego08.items.enchantments.ZEnchantments;
import fr.maxlego08.items.placeholder.LocalPlaceholder;
import fr.maxlego08.items.save.Config;
import fr.maxlego08.items.save.MessageLoader;
import fr.maxlego08.items.zcore.ZPlugin;

public class ItemsPlugin extends ZPlugin {

    private final ItemManager itemManager = new ZItemManager(this);
    private final Enchantments enchantments = new ZEnchantments();
    private ItemComponent itemComponent;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zitems");

        this.preEnable();

        this.enchantments.register();
        this.itemComponent = isPaperVersion() ? new PaperComponent() : new SpigotComponent();

        this.registerCommand("zitems", new CommandItem(this), "items", "zit");

        this.addSave(Config.getInstance());
        this.addSave(new MessageLoader(this));
        this.itemManager.loadItems();

        this.loadFiles();

        this.postEnable();
    }

    @Override
    public void onDisable() {

        this.preDisable();

        this.saveFiles();

        this.postDisable();
    }

    public ItemComponent getItemComponent() {
        return itemComponent;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public Enchantments getEnchantments() {
        return enchantments;
    }
}
