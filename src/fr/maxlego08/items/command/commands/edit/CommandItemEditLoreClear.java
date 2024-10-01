package fr.maxlego08.items.command.commands.edit;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.enums.Permission;
import fr.maxlego08.items.zcore.utils.commands.CommandType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CommandItemEditLoreClear extends VCommand {

    public CommandItemEditLoreClear(ItemsPlugin plugin) {
        super(plugin);
        this.addSubCommand("clear");
        this.setPermission(Permission.ZITEMS_EDIT_LORE);
        this.setDescription(Message.DESCRIPTION_ITEM_LORE_CLEAR);
        this.onlyPlayers();

    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        ItemStack itemStack = this.player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            message(sender, Message.COMMAND_ITEM_EMPTY);
            return CommandType.DEFAULT;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        ItemComponent itemComponent = this.plugin.getItemComponent();
        itemComponent.setLore(itemMeta, new ArrayList<>());
        itemStack.setItemMeta(itemMeta);

        message(sender, Message.COMMAND_ITEM_LORE_CLEAR);

        return CommandType.SUCCESS;
    }

}
