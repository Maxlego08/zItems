package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItemReload extends VCommand {

    public CommandItemReload(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_RELOAD);
        this.addSubCommand("reload", "rl");
        this.setDescription(Message.DESCRIPTION_RELOAD);
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        plugin.reloadFiles();
        plugin.getServer().clearRecipes();
        plugin.getItemManager().loadItems();
        message(sender, Message.RELOAD);

        return CommandType.SUCCESS;
    }

}
