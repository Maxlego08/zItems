package fr.maxlego08.items.api.configurations.commands;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record CommandsConfiguration(List<ItemCommand> commands, boolean needConfirmation) {

    public static CommandsConfiguration loadCommandsConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        List<ItemCommand> itemCommands = new ArrayList<>();

        if (!configuration.contains(path + "commands")) {
            return new CommandsConfiguration(itemCommands, false);
        }

        if (!configuration.isList(path + "commands")) {
            throw new IllegalArgumentException("Invalid command configuration in " + fileName + " at " + path);
        }

        boolean needConfirm = configuration.getBoolean("commands-need-confirm", false);

        for (Object commandConfig : configuration.getList(path + "commands")) {

            if (!(commandConfig instanceof Map<?, ?>)) {
                throw new IllegalArgumentException("Invalid command configuration in " + fileName + " at " + path);
            }

            Map<String, Object> commandMap = (Map<String, Object>) commandConfig;
            CommandSender sender = CommandSender.valueOf(((String) commandMap.get("sender")).toUpperCase());
            Action action = commandMap.containsKey("action") ? Action.valueOf(((String) commandMap.get("action")).toUpperCase()) : Action.CLICK;

            List<String> commands = new ArrayList<>();
            if (commandMap.containsKey("command")) {
                commands.add(((String) commandMap.get("command")));
            } else if (commandMap.containsKey("commands")) {
                commands = ((List<String>) commandMap.get("commands"));
            }

            List<String> messages = new ArrayList<>();
            if (commandMap.containsKey("messages")) {
                messages = ((List<String>) commandMap.get("messages"));
            }

            ItemCommand.ItemDamage damage = null;
            long cooldown = 0;
            if (commandMap.containsKey("cooldown")) {
                try {
                    cooldown = Long.parseLong(commandMap.get("cooldown").toString());
                    if (cooldown < 0) {
                        throw new IllegalArgumentException("Invalid cooldown in " + fileName + " at " + path);
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid cooldown in " + fileName + " at " + path);
                }
            }

            if (commandMap.containsKey("damage")) {
                Map<String, Object> damageMap = (Map<String, Object>) commandMap.get("damage");
                ItemCommand.DamageType type = ItemCommand.DamageType.valueOf(((String) damageMap.get("type")).toUpperCase());
                int damageAmount;
                try {
                    damageAmount = Integer.parseInt(damageMap.get("quantity").toString());
                    if (damageAmount < 0) {
                        throw new IllegalArgumentException("Invalid damage amount in " + fileName + " at " + path);
                    }
                } catch (NumberFormatException e) {
                    if (damageMap.get("quantity").toString().equalsIgnoreCase("all")) {
                        damageAmount = -1;
                    } else {
                        throw new IllegalArgumentException("Invalid damage amount in " + fileName + " at " + path);
                    }
                }
                damage = new ItemCommand.ItemDamage(type, damageAmount);
            }

            itemCommands.add(new ItemCommand(sender, action, commands, messages, damage, cooldown));
        }

        return new CommandsConfiguration(itemCommands, needConfirm);
    }
}
