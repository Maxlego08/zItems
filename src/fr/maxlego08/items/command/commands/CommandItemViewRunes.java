package fr.maxlego08.items.command.commands;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.command.VCommand;
import fr.maxlego08.items.zcore.enums.Message;
import fr.maxlego08.items.zcore.utils.commands.CommandType;

import java.util.ArrayList;
import java.util.List;

public class CommandItemViewRunes extends VCommand {

    public CommandItemViewRunes(ItemsPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_VIEW_RUNES);
        this.addSubCommand("viewrunes", "vr");
        this.onlyPlayers();
    }

    @Override
    protected CommandType perform(ItemsPlugin plugin) {
        var optRunes = plugin.getRuneManager().getRunes(this.player.getInventory().getItemInMainHand());
        if(optRunes.isPresent()) {
            List<String> runeLore = Message.RUNE_LORE.getMessages();
            //remove first empty line
            runeLore.removeFirst();

            List<String> formattedLore = new ArrayList<>();

            runeLore.forEach(line -> formattedLore.add(color(line)));
            for (Rune rune : optRunes.get()) {
                formattedLore.add(color(getMessage(Message.RUNE_LINE, "%rune%", rune.getDisplayName())));
            }
            this.player.sendMessage(formattedLore.toArray(new String[0]));
        }
        return CommandType.SUCCESS;
    }
}
