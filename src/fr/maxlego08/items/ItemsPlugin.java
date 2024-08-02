package fr.maxlego08.items;

import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.command.commands.CommandItems;
import fr.maxlego08.items.components.PaperComponent;
import fr.maxlego08.items.components.SpigotComponent;
import fr.maxlego08.items.placeholder.LocalPlaceholder;
import fr.maxlego08.items.save.Config;
import fr.maxlego08.items.save.MessageLoader;
import fr.maxlego08.items.zcore.ZPlugin;

public class ItemsPlugin extends ZPlugin {

    private ItemComponent itemComponent;

    @Override
    public void onEnable() {

        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();
        placeholder.setPrefix("zitems");

        this.preEnable();

        this.itemComponent = isPaperVersion() ? new PaperComponent() : new SpigotComponent();

        this.registerCommand("zitems", new CommandItems(this), "items", "zit");

        this.addSave(Config.getInstance());
        this.addSave(new MessageLoader(this));

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
}
