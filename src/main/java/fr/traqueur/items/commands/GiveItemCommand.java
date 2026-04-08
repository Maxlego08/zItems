package fr.traqueur.items.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.items.Messages;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.items.Item;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GiveItemCommand extends Command<@NotNull ItemsPlugin> {

    /**
     * The constructor of the command.
     *
     * @param plugin The plugin that owns the command.
     */
    public GiveItemCommand(ItemsPlugin plugin) {
        super(plugin, "item.give");
        this.setDescription("Give a custom item to a player");
        this.setPermission("items.command.item.give");
        this.addArgs("player", Player.class, "item", Item.class);
        this.addOptionalArg("amount", Integer.class, (sender, lastArgs) -> List.of("1", "16", "64"));
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player target = arguments.get("player");
        Item item = arguments.get("item");
        Optional<Integer> amountOpt = arguments.getOptional("amount");
        int amount = amountOpt.orElse(1);

        // Validate amount
        if (amount <= 0) {
            Messages.ITEM_GIVE_INVALID_AMOUNT.send(sender);
            return;
        }

        // Build the item
        ItemStack itemStack = item.build(target, amount);

        // Give the item to the player
        target.getInventory().addItem(itemStack);

        // Send success messages
        Messages.ITEM_GIVEN.send(
                sender,
                Placeholder.parsed("player", target.getName()),
                Placeholder.parsed("item", item.representativeName()),
                Placeholder.parsed("amount", String.valueOf(amount))
        );

        if (!sender.equals(target)) {
            Messages.ITEM_RECEIVED.send(
                    target,
                    Placeholder.parsed("item", item.representativeName()),
                    Placeholder.parsed("amount", String.valueOf(amount))
            );
        }
    }
}