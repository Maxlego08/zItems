package fr.traqueur.items.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.items.Messages;
import fr.traqueur.items.ZItems;
import fr.traqueur.items.api.ItemsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuiCommand extends Command<@NotNull ItemsPlugin> {

    /**
     * The constructor of the command.
     *
     * @param plugin The plugin that owns the command.
     */
    public GuiCommand(ItemsPlugin plugin) {
        super(plugin, "gui");
        this.setDescription("Open the items GUI");
        this.setPermission("items.command.gui");
        this.setGameOnly(true);
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player player = (Player) sender;
        ItemsPlugin plugin = this.getPlugin();
        // Reset navigation state so the GUI always opens at root
        player.removeMetadata("zitems-current-folder", plugin);
        player.removeMetadata("zitems-showing-effects", plugin);
        player.removeMetadata("zitems-current-effects-folder", plugin);
        player.removeMetadata("zitems-nav-stack", plugin);
        plugin.getInventoryManager().openInventory(player, "items_list");
    }
}