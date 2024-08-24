package fr.maxlego08.items.api.configurations.commands;

public record ItemCommand(CommandSender sender, Action action, String command, ItemDamage damage, long cooldown) {

    public enum DamageType {
        AMOUNT,
        DURABILITY
    }

    public record ItemDamage(DamageType type, int damage) { }

}