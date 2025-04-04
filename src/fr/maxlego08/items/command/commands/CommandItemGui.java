package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import fr.maxlego08.menu.api.InventoryManager;

public class CommandItemGui extends VCommand {

    public CommandItemGui(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_GUI);
        this.addSubCommand("gui");
        this.setDescription(Message.DESCRIPTION_GUI);
        this.onlyPlayers();
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        plugin.getInventoryManager().openInventory(player, "items_gui");

        return CommandType.SUCCESS;
    }
}