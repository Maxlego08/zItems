package fr.maxlego08.items.command.commands.edit;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandItemEditCustomModelData extends VCommand {

    public CommandItemEditCustomModelData(ItemsPlugin plugin) {
        super(plugin);
        this.addSubCommand("custommodeldata", "cmd", "modelid");
        this.setPermission(Permission.ZITEMS_EDIT_CUSTOM_MODEL_DATA);
        this.setDescription(Message.DESCRIPTION_ITEM_CUSTOM_MODEL_DATA);
        this.addOptionalArg("id");
        this.onlyPlayers();

    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        int modelId = this.argAsInteger(0);
        ItemStack itemStack = this.player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            message(sender, Message.COMMAND_ITEM_EMPTY);
            return CommandType.DEFAULT;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setCustomModelData(modelId);
        itemStack.setItemMeta(itemMeta);

        message(sender, Message.COMMAND_ITEM_CUSTOM_MODEL_DATA, "%custom-model-data%", modelId);

        return CommandType.SUCCESS;
    }

}
