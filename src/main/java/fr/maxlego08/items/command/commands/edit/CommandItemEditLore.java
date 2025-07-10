package fr.maxlego08.items.command.commands.edit;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItemEditLore extends VCommand {

    public CommandItemEditLore(ItemsPlugin plugin) {
        super(plugin);
        this.addSubCommand("lore");
        this.setPermission(Permission.ZITEMS_EDIT_LORE);
        this.setDescription(Message.DESCRIPTION_ITEM_LORE);
        this.addSubCommand(new CommandItemEditLoreAdd(plugin));
        this.addSubCommand(new CommandItemEditLoreClear(plugin));
        this.addSubCommand(new CommandItemEditLoreSet(plugin));
        this.onlyPlayers();

    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {
        syntaxMessage();
        return CommandType.SUCCESS;
    }

}
