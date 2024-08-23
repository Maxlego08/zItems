package fr.maxlego08.items.api.configurations.commands;

public record ItemCommand(CommandSender sender, Action action, String command) {}