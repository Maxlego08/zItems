package fr.maxlego08.items.api.configurations.commands;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record CommandsConfiguration(List<ItemCommand> commands) {

    public static CommandsConfiguration loadCommandsConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        List<ItemCommand> commands = new ArrayList<>();

        if(!configuration.contains(path + "commands")) {
            return new CommandsConfiguration(commands);
        }

        if(!configuration.isList(path + "commands")) {
            throw new IllegalArgumentException("Invalid command configuration in " + fileName + " at " + path);
        }

        for (Object commandConfig : configuration.getList(path + "commands")) {
            if(!(commandConfig instanceof Map<?,?>)) {
                throw  new IllegalArgumentException("Invalid command configuration in " + fileName + " at " + path);
            }
            Map<String, Object> commandMap = (Map<String, Object>) commandConfig;
            CommandSender sender = CommandSender.valueOf(((String )commandMap.get("sender")).toUpperCase());
            Action action = Action.valueOf(((String )commandMap.get("action")).toUpperCase());
            String command = ((String)commandMap.get("command"));
            ItemCommand.ItemDamage damage = null;
            if(commandMap.containsKey("damage")) {
                Map<String, Object> damageMap = (Map<String, Object>) commandMap.get("damage");
                ItemCommand.DamageType type = ItemCommand.DamageType.valueOf(((String)damageMap.get("type")).toUpperCase());
                int damageAmount;
                try {
                    damageAmount = Integer.parseInt(damageMap.get("quantity").toString());
                    if(damageAmount < 0) {
                        throw new IllegalArgumentException("Invalid damage amount in " + fileName + " at " + path);
                    }
                } catch (NumberFormatException e) {
                    if(damageMap.get("quantity").toString().equalsIgnoreCase("all")) {
                        damageAmount = -1;
                    } else {
                        throw new IllegalArgumentException("Invalid damage amount in " + fileName + " at " + path);
                    }
                }
                damage = new ItemCommand.ItemDamage(type, damageAmount);
            }

            commands.add(new ItemCommand(sender, action, command, damage));
        }

        return new CommandsConfiguration(commands);
    }
}
