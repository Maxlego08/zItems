package fr.maxlego08.items.command.commands.edit;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItemEdit extends VCommand {

    public CommandItemEdit(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_EDIT);
        this.addSubCommand("edit");
        this.addSubCommand(new CommandItemEditName(plugin));
        this.addSubCommand(new CommandItemEditLore(plugin));
        this.addSubCommand(new CommandItemEditCustomModelData(plugin));
        this.addSubCommand(new CommandItemEditInfo(plugin));
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {
        syntaxMessage();
        return CommandType.SUCCESS;
    }

}
