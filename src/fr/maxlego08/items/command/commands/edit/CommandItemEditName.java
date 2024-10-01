package fr.maxlego08.items.command.commands.edit;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandItemEditName extends VCommand {

    public CommandItemEditName(ItemsPlugin plugin) {
        super(plugin);
        this.addSubCommand("name");
        this.setPermission(Permission.ZITEMS_EDIT_NAME);
        this.setDescription(Message.DESCRIPTION_ITEM_NAME);
        this.addOptionalArg("name");
        this.setExtendedArgs(true);
        this.onlyPlayers();

    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        String itemName = this.getArgs(2);
        ItemStack itemStack = this.player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            message(sender, Message.COMMAND_ITEM_EMPTY);
            return CommandType.DEFAULT;
        }

        boolean isReset = itemName.isEmpty();
        ItemMeta itemMeta = itemStack.getItemMeta();

        ItemComponent itemComponent = this.plugin.getItemComponent();
        itemComponent.setItemName(itemMeta, itemName);

        itemStack.setItemMeta(itemMeta);

        message(sender, isReset ? Message.COMMAND_ITEM_CLEAR : Message.COMMAND_ITEM_SET, "%name%", itemName);

        return CommandType.SUCCESS;
    }

}
