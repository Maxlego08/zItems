package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.utils.Plugins;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.command.commands.edit.CommandItemEdit;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItem extends VCommand {

    public CommandItem(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_USE);
        this.addSubCommand(new CommandItemReload(plugin));
        this.addSubCommand(new CommandItemApplyRune(plugin));
        this.addSubCommand(new CommandItemViewRunes(plugin));
        this.addSubCommand(new CommandItemEdit(plugin));
        this.addSubCommand(new CommandItemGui(plugin));
        this.addSubCommand(new CommandItemOpenFolders(plugin));

        if (Plugins.ZESSENTIALS.isEnable()) {
            this.addSubCommand(new CommandItemMail(plugin));
            this.addSubCommand(new CommandItemGiveOrMail(plugin));
        } else {
            this.addSubCommand(new CommandItemGive(plugin));
        }
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {
        syntaxMessage();
        return CommandType.SUCCESS;
    }

}
