package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItem extends VCommand {

    public CommandItem(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_USE);
        this.addSubCommand(new CommandItemReload(plugin));
        this.addSubCommand(new CommandItemApplyRune(plugin));
        this.addSubCommand(new CommandItemGive(plugin));
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {
        syntaxMessage();
        return CommandType.SUCCESS;
    }

}
