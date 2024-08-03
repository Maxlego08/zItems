package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandItemGive extends VCommand {

    public CommandItemGive(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_GIVE);
        this.addSubCommand("give", "g");
        this.setDescription(Message.DESCRIPTION_GIVE);
        this.addRequireArg("item", (a, b) -> plugin.getItemManager().getItemNames());
        this.addOptionalArg("player");
        this.addOptionalArg("amount", (a, b) -> Arrays.asList("1", "8", "16", "32", "48", "68"));
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        String itemName = this.argAsString(0);
        Player player = this.argAsPlayer(1, this.player);
        int amount = this.argAsInteger(2, 0);

        if (player == null) return CommandType.SYNTAX_ERROR;

        plugin.getItemManager().giveItem(sender, player, itemName, amount);

        return CommandType.SUCCESS;
    }

}
