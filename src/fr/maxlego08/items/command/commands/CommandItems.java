package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItems extends VCommand {

	public CommandItems(ItemsPlugin plugin) {
		super(plugin);
		this.setPermission(Permission.ZITEMS_USE);
		this.addSubCommand(new CommandItemsReload(plugin));
	}

	@Override
	protected CommandType perform(ItemsPlugin plugin) {
		syntaxMessage();
		return CommandType.SUCCESS;
	}

}
