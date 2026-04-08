package fr.traqueur.items.commands;

import fr.traqueur.commands.api.arguments.Arguments;
import fr.traqueur.commands.spigot.Command;
import fr.traqueur.items.Messages;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.managers.EffectsManager;
import fr.traqueur.items.api.registries.EffectsRegistry;
import fr.traqueur.items.api.registries.Registry;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class GiveEffectCommand extends Command<@NotNull ItemsPlugin> {

    /**
     * The constructor of the command.
     *
     * @param plugin The plugin that owns the command.
     */
    public GiveEffectCommand(ItemsPlugin plugin) {
        super(plugin, "effect.give");
        this.setDescription("Give an effect item to a player");
        this.setPermission("items.command.effect.give");
        this.addArg("player", Player.class);
        this.addArg("effect", Effect.class, (sender, lastArgs) -> {
            EffectsRegistry effectsRegistry = Registry.get(EffectsRegistry.class);
            return effectsRegistry.getAll().stream().filter(effect -> effect.representation() != null).map(Effect::id).toList();
        });
        this.addOptionalArg("amount", Integer.class, (sender, lastArgs) -> List.of("1", "16", "64"));
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Player target = arguments.get("player");
        Effect effect = arguments.get("effect");
        Optional<Integer> amountOpt = arguments.getOptional("amount");
        int amount = amountOpt.orElse(1);

        // Validate amount
        if (amount <= 0) {
            Messages.EFFECT_GIVE_INVALID_AMOUNT.send(sender);
            return;
        }

        // Check if effect has representation
        if (effect.representation() == null) {
            Messages.EFFECT_NO_REPRESENTATION.send(sender, Placeholder.component("effect", effect.displayName()));
            return;
        }

        EffectsManager effectsManager = this.getPlugin().getManager(EffectsManager.class);
        if (effectsManager == null) {
            Messages.FAILED_TO_OPEN_GUI.send(sender);
            return;
        }

        // Create effect item
        ItemStack effectItem = effectsManager.createEffectItem(effect, target);
        effectItem.setAmount(amount);

        // Give the effect item to the player
        var rest = target.getInventory().addItem(effectItem);
        rest.values().forEach(dropped ->
                target.getWorld().dropItem(target.getLocation(), dropped)
        );

        // Send success messages
        Messages.EFFECT_GIVEN.send(
                sender,
                Placeholder.parsed("player", target.getName()),
                Placeholder.component("effect", effect.displayName()),
                Placeholder.parsed("amount", String.valueOf(amount))
        );

        if (!sender.equals(target)) {
            Messages.EFFECT_RECEIVED.send(
                    target,
                    Placeholder.component("effect", effect.displayName()),
                    Placeholder.parsed("amount", String.valueOf(amount))
            );
        }
    }
}