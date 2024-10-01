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
import java.util.List;

public class CommandItemEditLoreSet extends VCommand {

    public CommandItemEditLoreSet(ItemsPlugin plugin) {
        super(plugin);
        this.addSubCommand("set");
        this.setPermission(Permission.ZITEMS_EDIT_LORE);
        this.setDescription(Message.DESCRIPTION_ITEM_LORE_SET);
        this.addRequireArg("index");
        this.addRequireArg("line");
        this.setExtendedArgs(true);
        this.onlyPlayers();

    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {

        int index = this.argAsInteger(0);
        String loreLine = this.getArgs(2);
        if (loreLine.isEmpty()) return CommandType.SYNTAX_ERROR;

        ItemStack itemStack = this.player.getInventory().getItemInMainHand();
        if (itemStack.getType().isAir()) {
            message(sender, Message.COMMAND_ITEM_EMPTY);
            return CommandType.DEFAULT;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        if (lore == null) lore = new ArrayList<>();

        if (lore.size() < index) {
            message(sender, Message.COMMAND_ITEM_LORE_SET_ERROR, "%line%", index);
            return CommandType.DEFAULT;
        }

        ItemComponent itemComponent = this.plugin.getItemComponent();
        itemComponent.setLoreIndex(itemMeta, index - 1, loreLine);

        itemStack.setItemMeta(itemMeta);

        message(sender, Message.COMMAND_ITEM_LORE_SET, "%text%", loreLine, "%line%", index);

        return CommandType.SUCCESS;
    }

}
