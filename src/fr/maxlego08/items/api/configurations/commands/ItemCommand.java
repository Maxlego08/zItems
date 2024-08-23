package fr.maxlego08.items.api.configurations.commands;

import io.papermc.paper.registry.keys.DamageTypeKeys;

public record ItemCommand(CommandSender sender, Action action, String command, ItemDamage damage, long cooldown) {

    public enum DamageType {
        AMOUNT,
        DURABILITY
    }

    public record ItemDamage(DamageType type, int damage) { }

}