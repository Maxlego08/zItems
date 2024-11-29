package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;
import fr.maxlego08.menu.api.InventoryManager;

public class CommandGui extends VCommand {

    private final ItemsPlugin plugin;

    public CommandGui(ItemsPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.setPermission(Permission.ZITEMS_GUI);
        this.addSubCommand("gui");
        this.setDescription(Message.DESCRIPTION_GUI);
        this.onlyPlayers();
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        if(Plugins.ZMENU.isEnable()) {
            this.getProvider(this.plugin, InventoryManager.class).openInventory(player, "items_gui");
        }

        return CommandType.SUCCESS;
    }
}