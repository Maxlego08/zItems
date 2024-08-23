package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

public class CommandItemApplyRune extends VCommand {

    public CommandItemApplyRune(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_RUNE_APPLY);
        this.addSubCommand("applyrune");
        this.setDescription(Message.DESCRIPTION_GIVE);
        this.addRequireArg("rune", (a, b) -> plugin.getRuneManager().getRunes().stream().map(Rune::getName).toList());
        this.onlyPlayers();
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        String runName = this.argAsString(0);
        plugin.getRuneManager().applyRune(player, runName);

        return CommandType.SUCCESS;
    }

}
