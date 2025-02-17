package fr.maxlego08.items.command.commands;

import fr.maxlego08.essentials.api.EssentialsPlugin;
import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CommandItemMail extends VCommand {

    public CommandItemMail(ItemsPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZITEMS_MAILBOX);
        this.addSubCommand("mail", "mailbox");
        this.setDescription(Message.DESCRIPTION_MAILBOX);
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

        var optional = plugin.getItemManager().getItem(itemName);
        if (optional.isEmpty()) {
            message(sender, Message.ITEM_NOT_FOUND, "%name%", itemName);
            return CommandType.DEFAULT;
        }

        var item = optional.get();
        ItemStack itemStack = item.build(player, amount);
        EssentialsPlugin essentialsPlugin = (EssentialsPlugin) plugin.getServer().getPluginManager().getPlugin("zEssentials");
        essentialsPlugin.addMailBoxItem(player.getUniqueId(), itemStack);

        message(sender, Message.ITEM_MAILBOX, "%name%", itemName, "%player%", player.getName());

        return CommandType.SUCCESS;
    }

}
