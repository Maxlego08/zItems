package fr.maxlego08.items.api.configurations.commands;

import java.util.List;

public record ItemCommand(CommandSender sender, Action action, List<String> commands, List<String> messages, ItemDamage damage, long cooldown) {

    public enum DamageType {
        AMOUNT,
        DURABILITY
    }

    public record ItemDamage(DamageType type, int damage) { }

}